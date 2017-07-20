package net.kuryshev;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class AwtView extends Frame {
    private Button doCheckButton;
    private TextArea input, good, bad;


    public AwtView() throws HeadlessException {
        super("Proxy Checker");
        setLayout(new FlowLayout());
        setSize(900, 400);
        addWindowListener(new MyWindowAdapter());
        input = new TextArea(20, 30);
        add(input);
        good = new TextArea(20, 30);
        add(good);
        bad = new TextArea(20, 30);
        add(bad);
        addCheckButton();
        setVisible(true);
    }

    private void addCheckButton() {
        doCheckButton = new Button("Проверить прокси");
        add(doCheckButton);
        doCheckButton.addActionListener((ea) -> {
            String inputString = input.getText();
            String[] proxies = inputString.split("\n");
            for (String proxy : proxies) {
                try {
                    if (ProxyChecker.isOk(proxy)) good.setText(good.getText() + proxy + "\n");
                } catch (Exception e) {
                    bad.setText(bad.getText() + proxy + " " + e.getMessage() + "\n");
                }
                repaint();
            }
        });
    }

    public static void main(String[] args) {
        new AwtView();
    }
}

class MyWindowAdapter extends WindowAdapter {
    @Override
    public void windowClosing(WindowEvent e) {
        System.exit(0);
    }
}
