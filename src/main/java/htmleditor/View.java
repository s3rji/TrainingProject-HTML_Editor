package htmleditor;

import htmleditor.listeners.FrameListener;
import htmleditor.listeners.TabbedPaneChangeListener;
import htmleditor.listeners.UndoListener;

import javax.swing.*;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class View extends JFrame implements ActionListener {
    private Controller controller;
    private JTabbedPane tabbedPane = new JTabbedPane();
    private JTextPane htmlTextPane = new JTextPane();
    private JEditorPane plainTextPane = new JEditorPane();
    private UndoManager undoManager = new UndoManager();
    private UndoListener undoListener = new UndoListener(undoManager);

    public View() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            ExceptionHandler.log(e);
        }
    }

    public Controller getController() {
        return controller;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public UndoListener getUndoListener() {
        return undoListener;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        String command = actionEvent.getActionCommand();
        switch (command) {
            case "Новый":
                controller.createNewDocument();
                break;
            case "Открыть":
                controller.openDocument();
                break;
            case "Сохранить":
                controller.saveDocument();
                break;
            case "Сохранить как...":
                controller.saveDocumentAs();
                break;
            case "Выход":
                controller.exit();
                break;
            case "О программе":
                showAbout();
                break;
        }
    }

    public void init() {
        initGui();
        addWindowListener(new FrameListener(this));
        setVisible(true);
    }

    public void initGui() {
        initMenuBar();
        initEditor();
        pack();
    }

    public void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        MenuHelper.initFileMenu(this, menuBar);
        MenuHelper.initEditMenu(this, menuBar);
        MenuHelper.initStyleMenu(this, menuBar);
        MenuHelper.initAlignMenu(this, menuBar);
        MenuHelper.initColorMenu(this, menuBar);
        MenuHelper.initFontMenu(this, menuBar);
        MenuHelper.initHelpMenu(this, menuBar);

        getContentPane().add(menuBar, BorderLayout.NORTH);
    }

    public void initEditor() {
        htmlTextPane.setContentType("text/html");

        JScrollPane scrollHtmlText = new JScrollPane(htmlTextPane);
        tabbedPane.addTab("HTML", scrollHtmlText);

        JScrollPane scrollPlainText = new JScrollPane(plainTextPane);
        tabbedPane.addTab("Текст", scrollPlainText);

        tabbedPane.setPreferredSize(new Dimension(600, 400));

        TabbedPaneChangeListener tabPaneChangeListener = new TabbedPaneChangeListener(this);
        tabbedPane.addChangeListener(tabPaneChangeListener);

        getContentPane().add(tabbedPane, BorderLayout.CENTER);
    }

    public void undo() {
        try {
            undoManager.undo();
        } catch (CannotUndoException e) {
            ExceptionHandler.log(e);
        }
    }

    public void redo() {
        try {
            undoManager.redo();
        } catch (CannotRedoException e) {
            ExceptionHandler.log(e);
        }
    }

    public void resetUndo() {
        undoManager.discardAllEdits();
    }

    public boolean canUndo() { return undoManager.canUndo(); }

    public boolean canRedo() {
        return undoManager.canRedo();
    }

    public boolean isHtmlTabSelected() {
        return tabbedPane.getSelectedIndex() == 0;
    }

    public void selectHtmlTab() {
        tabbedPane.setSelectedIndex(0);
        resetUndo();
    }

    public void selectedTabChanged() {
       if (tabbedPane.getSelectedIndex() == 0)
           controller.setPlainText(plainTextPane.getText());
       else if (tabbedPane.getSelectedIndex() == 1)
           plainTextPane.setText(controller.getPlainText());

       resetUndo();
    }

    public void update() {
        htmlTextPane.setDocument(controller.getDocument());
    }

    public void showAbout() {
        JOptionPane.showMessageDialog(this, "Программа для редактирования HTML документа",
                "О программе", JOptionPane.INFORMATION_MESSAGE);
    }

    public void exit() {
        controller.exit();
    }
}
