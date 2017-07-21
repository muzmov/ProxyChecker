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
    private static final int DEFAULT_TIMEOUT = 2000;
    private static final int DEFAULT_NUM_THREADS = 5;
    private static final String DEFAULT_URL = "http://yandex.ru";
    private static final int MIN_THREADS = 1, MAX_THREADS = 100;
    private static final int MIN_TIMEOUT = 1, MAX_TIMEOUT = 10000;
    private ExecutorService poolExecutor = Executors.newFixedThreadPool(DEFAULT_NUM_THREADS);

    private Button checkButton, stopButton;
    private TextArea inputArea, goodProxiesArea, badProxiesArea;
    private TextField threadsField, urlField, timeoutField;
    private String url;
    private int numThreads, timeout;
    private ProxyChecker proxyChecker;

    public AwtView() throws HeadlessException {
        super("Proxy Checker");
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        setLayout(new BorderLayout());
        setLocationByPlatform(true);
        setSize(880, 410);
        setResizable(false);
        setTextFields();
        setAreas();
        setButtons();
        setVisible(true);
    }

    private void setTextFields() {
        urlField = new TextField(DEFAULT_URL);
        threadsField = new TextField(DEFAULT_NUM_THREADS + "");
        timeoutField = new TextField(DEFAULT_TIMEOUT + "");

        Panel panel = new Panel();
        panel.setSize(170,350);
        GridBagLayout layout = new GridBagLayout();

        panel.setLayout(layout);
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new Label("URL: "),gbc);

        gbc.insets = new Insets(0, 0, 100, 0);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(urlField, gbc);

        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new Label("Число потоков (" + MIN_THREADS + " - " + MAX_THREADS + "): "), gbc);

        gbc.insets = new Insets(0, 0, 100, 0);
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(threadsField, gbc);

        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new Label("Таймаут в мс (" + MIN_TIMEOUT + " - " + MAX_TIMEOUT + "): "), gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(timeoutField, gbc);

        add(panel, BorderLayout.WEST);

    }

    private void setAreas() {
        Panel panel = new Panel();
        panel.setSize(710,350);
        GridBagLayout layout = new GridBagLayout();

        panel.setLayout(layout);
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new Label("Прокси для проверки: "),gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(new Label("Хорошие: "),gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        panel.add(new Label("Плохие: "),gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 1;
        inputArea = new TextArea(20, 30);
        panel.add(inputArea, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        goodProxiesArea = new TextArea(20, 30);
        panel.add(goodProxiesArea, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        badProxiesArea = new TextArea(20, 30);
        panel.add(badProxiesArea, gbc);
        add(panel, BorderLayout.CENTER);
    }

    private void setButtons() {
        checkButton = new Button("Проверить прокси");
        checkButton.addActionListener(new CheckActionListener());
        stopButton = new Button("Остановить проверку");
        stopButton.addActionListener(new StopActionListener());
        stopButton.setEnabled(false);

        Container buttonContainer = new Container();
        buttonContainer.setLayout(new FlowLayout());
        buttonContainer.add(checkButton);
        buttonContainer.add(stopButton);
        add(buttonContainer, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        new AwtView();
    }

    class CheckActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ae) {
            checkParams();
            proxyChecker = new ProxyChecker(url, timeout);
            new Thread(this::run).start();
        }

        void run() {
            String inputString = inputArea.getText();
            String[] proxies = inputString.split("\n");
            checkButton.setEnabled(false);
            stopButton.setEnabled(true);
            poolExecutor = Executors.newFixedThreadPool(numThreads);
            for (String proxy : proxies)
                poolExecutor.submit(new Task(proxy));
            poolExecutor.shutdown();
            try {
                poolExecutor.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
            } catch (InterruptedException e) {/*NOP*/}
            checkButton.setEnabled(true);
            stopButton.setEnabled(false);
        }
    }

    private void checkParams() {
        url = urlField.getText();
        if (url.isEmpty())
            url = DEFAULT_URL;
        try {
            timeout = Integer.parseInt(timeoutField.getText());
        } catch (NumberFormatException e) {
            timeout = DEFAULT_TIMEOUT;
        }
        if (timeout < MIN_TIMEOUT || timeout > MAX_TIMEOUT)
            timeout = DEFAULT_TIMEOUT;
        try {
            numThreads = Integer.parseInt(threadsField.getText());
        } catch (NumberFormatException e) {
            numThreads = DEFAULT_NUM_THREADS;
        }
        if (numThreads < MIN_THREADS || numThreads > MAX_THREADS)
            numThreads = DEFAULT_NUM_THREADS;
        urlField.setText(url);
        timeoutField.setText(timeout + "");
        threadsField.setText(numThreads + "");
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
                if (proxyChecker.isOk(proxy)) goodProxiesArea.setText(goodProxiesArea.getText() + proxy + "\n");
            } catch (Exception e) {
                if (!proxy.isEmpty())
                    badProxiesArea.setText(badProxiesArea.getText() + proxy + " " + e.getMessage() + "\n");
            }
        }
    }
}
