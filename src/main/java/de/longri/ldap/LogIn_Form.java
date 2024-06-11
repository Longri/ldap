package de.longri.ldap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;


public class LogIn_Form extends JFrame implements ActionListener, KeyListener {
    Container container = getContentPane();
    JLabel userLabel = new JLabel("USERNAME");
    JLabel passwordLabel = new JLabel("PASSWORD");
    JTextField userTextField = new JTextField();
    JPasswordField passwordField = new JPasswordField();
    JButton loginButton = new JButton("LOGIN");
    JCheckBox showPassword = new JCheckBox("Show Password");
    JCheckBox rememberMe = new JCheckBox("Remember Me");
    ImagePanel logo = new ImagePanel(getClass().getClassLoader().getResourceAsStream("biotrics_logo.png"));
    CallBack callBack;
    LdapConfig ldapConfig;

    public interface CallBack {
        public void callBack(String user, boolean rememberMe, Authentication groups);
    }

    public LogIn_Form(String title, LdapConfig config, CallBack callBack) {
        this(title, config, callBack, true);
    }

    public LogIn_Form(String title, LdapConfig config, CallBack callBack, boolean withRemember) {
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/itIcon16.png")));
        this.ldapConfig = config;
        this.setTitle(title);
        this.setBounds(10, 10, 450, 450);
        this.setResizable(false);

        setLayoutManager();
        addComponentsToContainer();
        addActionEvent();
        this.callBack = callBack;

        this.rememberMe.setSelected(ldapConfig.getRememberMe());
        this.rememberMe.setVisible(withRemember);
        if (this.ldapConfig.getUSER_PRINCIPAL_NAME() != null && !this.ldapConfig.getUSER_PRINCIPAL_NAME().isEmpty()) {
            userTextField.setText(this.ldapConfig.getUSER_PRINCIPAL_NAME());
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    SwingUtilities.invokeLater(() -> {
                        passwordField.setFocusable(true);
                        passwordField.requestFocus();
                    });
                }
            }, 100);

        }
    }

    public void setLogo(BufferedImage image) {
        container.remove(logo);
        logo = new ImagePanel(image);
        container.add(logo);
        container.doLayout();
    }

    public void setLayoutManager() {
        container.setLayout(new LayoutManager() {
            @Override
            public void addLayoutComponent(String name, Component comp) {

            }

            @Override
            public void removeLayoutComponent(Component comp) {

            }

            @Override
            public Dimension preferredLayoutSize(Container parent) {
                return null;
            }

            @Override
            public Dimension minimumLayoutSize(Container parent) {
                return null;
            }

            @Override
            public void layoutContainer(Container parent) {

                // remember x/y is the upper left corner

                int margin = 30;
                int center = parent.getWidth() / 2;

                int top = margin;
                logo.setBounds(center - 50, top, 100, 100);

                // calculate label width
                int lblWidth = parent.getWidth() - (2 * margin);


                top += logo.getHeight() + margin;
                userLabel.setBounds(margin + 5, top, lblWidth, 15);

                top += userLabel.getHeight();
                userTextField.setBounds(margin, top, lblWidth, 40);

                top += userTextField.getHeight() + margin;
                passwordLabel.setBounds(margin + 5, top, lblWidth, 15);

                top += passwordLabel.getHeight();
                passwordField.setBounds(margin, top, lblWidth, 40);

                top += passwordField.getHeight();
                showPassword.setBounds(margin, top, lblWidth, 30);


                top += showPassword.getHeight() + margin;

                loginButton.setBounds(margin, top, 100, 50);
                rememberMe.setBounds((int) (loginButton.getBounds().getMaxX() + margin), top, lblWidth - (2 * margin) - loginButton.getWidth(), 50);

            }
        });
    }

    public void addComponentsToContainer() {
        container.add(userLabel);
        container.add(passwordLabel);
        container.add(userTextField);
        container.add(passwordField);
        container.add(showPassword);
        container.add(loginButton);
        container.add(rememberMe);
        container.add(logo);
    }

    public void addActionEvent() {
        loginButton.addActionListener(this);
        rememberMe.addActionListener(this);
        showPassword.addActionListener(this);
        passwordField.addKeyListener(this);
        this.addKeyListener(this);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            logIn();
        }
        if (e.getSource() == rememberMe) {
            //todo
        }
        if (e.getSource() == showPassword) {
            if (showPassword.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('*');
            }


        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (!e.isConsumed() && e.getKeyChar() == 10 /*like enter*/) {
            e.consume();
            logIn();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    private void logIn() {
        this.ldapConfig.setUSER_PRINCIPAL_NAME(userTextField.getText());

        try {
            Authentication auth = new Authentication(this.ldapConfig);
            if (auth.isAuthorised(passwordField.getPassword())) {
                if (callBack != null)
                    callBack.callBack(this.ldapConfig.getUSER_PRINCIPAL_NAME(), rememberMe.isSelected(), auth);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Username or Password");
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }
}

