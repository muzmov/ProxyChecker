package net.kuryshev;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AwtView extends Frame {
    private static final int NUM_THREADS = 5;
    private ExecutorService poolExecutor = Executors.newFixedThreadPool(NUM_THREADS);

    private Button checkButton, stopButton;
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
        setButtons();
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

    private void setButtons() {
        Container buttonContainer = new Container();
        buttonContainer.setLayout(new FlowLayout());
        checkButton = new Button("Проверить прокси");
        checkButton.addActionListener(new CheckActionListener());
        buttonContainer.add(checkButton);
        stopButton = new Button("Остановить проверку");
        stopButton.addActionListener(new StopActionListener());
        stopButton.setEnabled(false);
        buttonContainer.add(stopButton);
        add(buttonContainer, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        new AwtView();
    }

    class CheckActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ae) {
            new Thread(this::run).start();
        }

        void run() {
            String inputString = inputArea.getText();
            String[] proxies = inputString.split("\n");
            checkButton.setEnabled(false);
            stopButton.setEnabled(true);
            poolExecutor = Executors.newFixedThreadPool(NUM_THREADS);
            for (String proxy : proxies) {
                Task task = new Task(proxy);
                poolExecutor.submit(task);
            }
            poolExecutor.shutdown();
            try {
                poolExecutor.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
            } catch (InterruptedException e) {/*NOP*/}
            checkButton.setEnabled(true);
            stopButton.setEnabled(false);
        }
    }

    class StopActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            poolExecutor.shutdownNow();
            checkButton.setEnabled(true);
            stopButton.setEnabled(false);
        }
    }

    class Task implements Runnable {
        private String proxy;

        Task(String proxy) {
            this.proxy = proxy;
        }

        @Override
        public void run() {
            try {
                if (ProxyChecker.isOk(proxy)) goodProxiesArea.setText(goodProxiesArea.getText() + proxy + "\n");
            } catch (Exception e) {
                if (!proxy.isEmpty())
                    badProxiesArea.setText(badProxiesArea.getText() + proxy + " " + e.getMessage() + "\n");
            }
        }
    }
}
