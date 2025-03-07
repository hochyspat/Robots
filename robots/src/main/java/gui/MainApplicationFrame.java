package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import javax.swing.*;

import log.Logger;

/**
 * Что требуется сделать:
 * 1. Метод создания меню перегружен функционалом и трудно читается. 
 * Следует разделить его на серию более простых методов (или вообще выделить отдельный класс).
 *
 */
public class MainApplicationFrame extends JFrame
{
    private final JDesktopPane desktopPane = new JDesktopPane();
    
    public MainApplicationFrame() {
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
            screenSize.width  - inset*2,
            screenSize.height - inset*2);

        setContentPane(desktopPane);
        
        
        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);

        GameWindow gameWindow = new GameWindow();
        gameWindow.setSize(400,  400);
        addWindow(gameWindow);

        setJMenuBar(generateMenuBar());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
    
    protected LogWindow createLogWindow()
    {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10,10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }
    
    protected void addWindow(JInternalFrame frame)
    {
        desktopPane.add(frame);
        frame.setVisible(true);
    }
    
//    protected JMenuBar createMenuBar() {
//        JMenuBar menuBar = new JMenuBar();
// 
//        //Set up the lone menu.
//        JMenu menu = new JMenu("Document");
//        menu.setMnemonic(KeyEvent.VK_D);
//        menuBar.add(menu);
// 
//        //Set up the first menu item.
//        JMenuItem menuItem = new JMenuItem("New");
//        menuItem.setMnemonic(KeyEvent.VK_N);
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(
//                KeyEvent.VK_N, ActionEvent.ALT_MASK));
//        menuItem.setActionCommand("new");
////        menuItem.addActionListener(this);
//        menu.add(menuItem);
// 
//        //Set up the second menu item.
//        menuItem = new JMenuItem("Quit");
//        menuItem.setMnemonic(KeyEvent.VK_Q);
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(
//                KeyEvent.VK_Q, ActionEvent.ALT_MASK));
//        menuItem.setActionCommand("quit");
////        menuItem.addActionListener(this);
//        menu.add(menuItem);
// 
//        return menuBar;
//    }
    
    private JMenuBar generateMenuBar()
    {
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
        Object[] options = {"Да", "Нет"};
        int YES = 0;
        int NO = 1;
        int response = JOptionPane.showOptionDialog(null,
                "Закрыть приложение?",
                "Подтверждение",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[NO]);

        if (response == YES) {
            System.exit(0);
        }
    }


    private JMenu initializeMenu(String menuName, String description) {
        JMenu someMenu = new JMenu(menuName);
        someMenu.setMnemonic(KeyEvent.VK_V);
        someMenu.getAccessibleContext().setAccessibleDescription(
                description);

        return someMenu;
    }
    
    private void setLookAndFeel(String className)
    {
        try
        {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        }
        catch (ClassNotFoundException | InstantiationException
            | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            // just ignore
        }
    }
}
