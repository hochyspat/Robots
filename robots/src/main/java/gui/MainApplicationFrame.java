package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import log.Logger;
import model.RobotModel;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.SwingUtilities;
import javax.swing.UnsupportedLookAndFeelException;


public class MainApplicationFrame extends JFrame {
    private final JDesktopPane desktopPane = new JDesktopPane();
    private final WindowConfigManager windowConfigManager = new WindowConfigManager();

    public MainApplicationFrame() {
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width - inset * 2,
                screenSize.height - inset * 2);

        setContentPane(desktopPane);


        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);

        RobotModel model = new RobotModel();
        GameWindow gameWindow = new GameWindow(model);
        CoordinatesWindow coordinatesWindow = new CoordinatesWindow();
        coordinatesWindow.setLocation(420, 10);
        coordinatesWindow.setSize(200, 100);
        model.addListener(coordinatesWindow);

        addWindow(gameWindow);
        addWindow(coordinatesWindow);

        gameWindow.setSize(400, 400);

        setJMenuBar(generateMenuBar());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                closeWithConfirmation();
            }
        });
    }

    protected LogWindow createLogWindow() {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10, 10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    protected void addWindow(JInternalFrame frame) {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    private JMenuBar generateMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        makeLookAndFeelMenu(menuBar);
        makeTestMenu(menuBar);
        makeSystemMenu(menuBar);

        return menuBar;
    }

    private void makeLookAndFeelMenu(JMenuBar menuBar) {
        JMenu lookAndFeelMenu = initializeMenu("Режим отображения",
                "Управление режимом отображения приложения");

        lookAndFeelMenu.add(createSystemLookAndFeelMenuItem("Системная схема"));
        lookAndFeelMenu.add(createSystemLookAndFeelMenuItem("Универсальная схема"));

        menuBar.add(lookAndFeelMenu);
    }

    private JMenuItem createSystemLookAndFeelMenuItem(String text) {
        JMenuItem systemLookAndFeel = new JMenuItem(text, KeyEvent.VK_S);
        systemLookAndFeel.addActionListener((event) -> {
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            this.invalidate();
        });

        return systemLookAndFeel;
    }

    private void makeTestMenu(JMenuBar menuBar) {
        JMenu testMenu = initializeMenu("Тесты", "Тестовые команды");

        testMenu.add(createTestMenuItem("Сообщение в лог", "Новая строка"));

        menuBar.add(testMenu);
    }

    private JMenuItem createTestMenuItem(String text, String logMessage) {
        JMenuItem addLogMessageItem = new JMenuItem(text, KeyEvent.VK_S);
        addLogMessageItem.addActionListener((event) -> {
            Logger.debug(logMessage);
        });

        return addLogMessageItem;
    }

    private void makeSystemMenu(JMenuBar menuBar) {
        JMenu systemMenu = initializeMenu("Система", "Управление системой");

        systemMenu.add(createSystemMenuCloseItem("Закрыть"));

        menuBar.add(systemMenu);
    }

    private JMenuItem createSystemMenuCloseItem(String text) {
        JMenuItem closeItem = new JMenuItem(text, KeyEvent.VK_S);
        closeItem.addActionListener((event) -> {
            closeWithConfirmation();
        });

        return closeItem;
    }

    private void closeWithConfirmation() {
        int response = JOptionPane.showOptionDialog(null,
                "Закрыть приложение?",
                "Подтверждение",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                Select.values(),
                Select.NO);

        if (response == JOptionPane.YES_OPTION) {
            windowConfigManager.saveAllWindows(desktopPane, this);
            System.exit(0);
        }
    }

    public void applyWindowSettings() {
        windowConfigManager.loadAllWindows(desktopPane, this);
    }


    private JMenu initializeMenu(String menuName, String description) {
        JMenu someMenu = new JMenu(menuName);
        someMenu.setMnemonic(KeyEvent.VK_V);
        someMenu.getAccessibleContext().setAccessibleDescription(
                description);

        return someMenu;
    }

    private void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (ClassNotFoundException | InstantiationException
                 | IllegalAccessException | UnsupportedLookAndFeelException e) {
        }
    }
}
