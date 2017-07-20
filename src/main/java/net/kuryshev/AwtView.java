package net.kuryshev;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class AwtView extends Frame {
    private Button doCheckButton;
    private TextArea inputArea, goodProxiesArea, badProxiesArea;


    public AwtView() throws HeadlessException {
        super("Proxy Checker");
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        setLayout(new BorderLayout());
        setSize(715, 400);
        setResizable(false);
        setLabels();
        setAreas();
        addCheckButton();
        setVisible(true);
    }

    private void setAreas() {
        Container areaContainer = new Container();
        areaContainer.setLayout(new FlowLayout());
        inputArea = new TextArea(20, 30);
        goodProxiesArea = new TextArea(20, 30);
        badProxiesArea = new TextArea(20, 30);
        areaContainer.add(inputArea);
        areaContainer.add(goodProxiesArea);
        areaContainer.add(badProxiesArea);
        add(areaContainer, BorderLayout.CENTER);
    }

    private void setLabels() {
        Container labelContainer = new Container();
        labelContainer.setLayout(new GridLayout(1, 3, 0, 0));
        labelContainer.add(new Label(" Прокси для проверки:"), 0);
        labelContainer.add(new Label(" Хорошие:"), 1);
        labelContainer.add(new Label(" Плохие:"), 2);
        add(labelContainer, BorderLayout.NORTH);
    }

    private void addCheckButton() {
        doCheckButton = new Button("Проверить прокси");
        add(doCheckButton, BorderLayout.SOUTH);
        doCheckButton.addActionListener((ea) -> {
            String inputString = inputArea.getText();
            String[] proxies = inputString.split("\n");
            doCheckButton.setEnabled(false);
            for (String proxy : proxies) {
                try {
                    if (ProxyChecker.isOk(proxy)) goodProxiesArea.setText(goodProxiesArea.getText() + proxy + "\n");
                } catch (Exception e) {
                    if (!proxy.isEmpty())
                        badProxiesArea.setText(badProxiesArea.getText() + proxy + " " + e.getMessage() + "\n");
                }
                repaint();
            }
            doCheckButton.setEnabled(true);
        });
    }

    public static void main(String[] args) {
        new AwtView();
    }
}
