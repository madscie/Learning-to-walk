package ROOFTOP;

import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Stack;
import javax.swing.*;
import javax.swing.border.*;

public class advancedcalc1 extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTextField displayScreen;
    private String currentExpression = "";
    private boolean isReadOnly = false;
    private boolean newInputExpected = true;
    private static final MathContext PRECISION = new MathContext(20);
    private static final int BUTTON_SIZE = 60;
    private static final int ICON_SIZE = 40;
    private static final BigDecimal PI = new BigDecimal("3.14159265358979323846");

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                advancedcalc1 frame = new advancedcalc1();
                frame.setVisible(true);
                frame.setLocationRelativeTo(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public advancedcalc1() {
        setTitle("KZJ-CYBERSPACE SCIENTIFIC");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(500, 600));
        
        JPanel contentPane = new JPanel(new BorderLayout(10, 10));
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        contentPane.setBackground(new Color(240, 240, 240));
        setContentPane(contentPane);

        // Display Screen
        displayScreen = new JTextField("0.0");
        displayScreen.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        displayScreen.setHorizontalAlignment(JTextField.RIGHT);
        displayScreen.setEditable(false);
        displayScreen.setBackground(Color.WHITE);
        displayScreen.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 10, 5, 10)
        ));
        contentPane.add(displayScreen, BorderLayout.NORTH);

        // Main button panel
        JPanel mainButtonPanel = new JPanel(new BorderLayout(5, 5));
        mainButtonPanel.setOpaque(false);

        // Scientific functions panel (left side)
        JPanel scientificPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        scientificPanel.setOpaque(false);
        
        // Scientific buttons
        scientificPanel.add(createButton("√", new Color(180, 230, 180), e -> handleSquareRoot()));
        scientificPanel.add(createButton("∛", new Color(180, 230, 180), e -> handleCubeRoot()));
        scientificPanel.add(createButton("x²", new Color(180, 230, 180), e -> handleSquare()));
        scientificPanel.add(createButton("x³", new Color(180, 230, 180), e -> handleCube()));
        scientificPanel.add(createButton("π", new Color(180, 230, 230), e -> handlePi()));
        scientificPanel.add(createButton("log", new Color(180, 230, 230), e -> handleLog()));
        scientificPanel.add(createButton("cos", new Color(180, 230, 230), e -> handleCos()));
        scientificPanel.add(createButton("tan", new Color(180, 230, 230), e -> handleTan()));
        scientificPanel.add(createButton("(", new Color(200, 200, 255), e -> appendToExpression("(")));
        scientificPanel.add(createButton(")", new Color(200, 200, 255), e -> appendToExpression(")")));

        mainButtonPanel.add(scientificPanel, BorderLayout.WEST);

        // Standard calculator panel
        JPanel buttonPanel = new JPanel(new GridLayout(5, 4, 5, 5));
        buttonPanel.setOpaque(false);
        
        // First row
        buttonPanel.add(createButton("AC", new Color(255, 100, 100), e -> handleAC()));
        buttonPanel.add(createButton("Mode", new Color(100, 200, 255), e -> handleMode()));
        buttonPanel.add(createButton("Del", new Color(255, 150, 50), e -> handleDelete()));
        buttonPanel.add(createButton("/", new Color(72, 61, 139), e -> appendOperator("/")));

        // Number rows
        buttonPanel.add(createButton("7", new Color(220, 220, 220), e -> appendToExpression("7")));
        buttonPanel.add(createButton("8", new Color(220, 220, 220), e -> appendToExpression("8")));
        buttonPanel.add(createButton("9", new Color(220, 220, 220), e -> appendToExpression("9")));
        buttonPanel.add(createButton("*", new Color(72, 61, 139), e -> appendOperator("*")));

        buttonPanel.add(createButton("4", new Color(220, 220, 220), e -> appendToExpression("4")));
        buttonPanel.add(createButton("5", new Color(220, 220, 220), e -> appendToExpression("5")));
        buttonPanel.add(createButton("6", new Color(220, 220, 220), e -> appendToExpression("6")));
        buttonPanel.add(createButton("-", new Color(72, 61, 139), e -> appendOperator("-")));

        buttonPanel.add(createButton("1", new Color(220, 220, 220), e -> appendToExpression("1")));
        buttonPanel.add(createButton("2", new Color(220, 220, 220), e -> appendToExpression("2")));
        buttonPanel.add(createButton("3", new Color(220, 220, 220), e -> appendToExpression("3")));
        buttonPanel.add(createButton("+", new Color(72, 61, 139), e -> appendOperator("+")));

        // Last row
        buttonPanel.add(createButton("0", new Color(220, 220, 220), e -> appendToExpression("0")));
        buttonPanel.add(createButton(".", new Color(220, 220, 220), e -> handleDecimal()));
        buttonPanel.add(createButton("Clr", new Color(255, 150, 50), e -> handleClear()));
        buttonPanel.add(createButton("=", new Color(50, 150, 50), e -> evaluateAndDisplay()));

        mainButtonPanel.add(buttonPanel, BorderLayout.CENTER);
        contentPane.add(mainButtonPanel, BorderLayout.CENTER);

        // Keyboard support
        displayScreen.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });

        pack();
    }

    private JButton createButton(String text, Color bgColor, ActionListener action) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Segoe UI", Font.BOLD, 18));
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(5, 5, 5, 5));
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.addActionListener(action);
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(brightenColor(bgColor, 0.2f));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }

    private Color brightenColor(Color color, float factor) {
        int r = Math.min(255, (int)(color.getRed() + (255 - color.getRed()) * factor));
        int g = Math.min(255, (int)(color.getGreen() + (255 - color.getGreen()) * factor));
        int b = Math.min(255, (int)(color.getBlue() + (255 - color.getBlue()) * factor));
        return new Color(r, g, b);
    }

    private void handleKeyPress(KeyEvent e) {
        if (isReadOnly) return;

        char keyChar = e.getKeyChar();
        int keyCode = e.getKeyCode();

        if (keyChar >= '0' && keyChar <= '9') {
            appendToExpression(String.valueOf(keyChar));
        }
        else if (keyChar == '+' || keyChar == '-' || keyChar == '*' || keyChar == '/') {
            appendOperator(String.valueOf(keyChar));
        }
        else if (keyChar == '.') {
            handleDecimal();
        }
        else if (keyChar == '=' || keyCode == KeyEvent.VK_ENTER) {
            evaluateAndDisplay();
        }
        else if (keyCode == KeyEvent.VK_BACK_SPACE) {
            handleDelete();
        }
        else if (keyCode == KeyEvent.VK_ESCAPE) {
            handleClear();
        }
        else if (keyChar == 's' || keyChar == 'S') {
            handleSquareRoot();
        }
        else if (keyChar == 'c' || keyChar == 'C') {
            handleCubeRoot();
        }
        else if (keyChar == 'p' || keyChar == 'P') {
            handlePi();
        }
        else if (keyChar == 'l' || keyChar == 'L') {
            handleLog();
        }
    }

    // Scientific functions
    private void handleSquareRoot() {
        try {
            BigDecimal value = currentExpression.isEmpty() ? BigDecimal.ZERO : new BigDecimal(currentExpression);
            if (value.compareTo(BigDecimal.ZERO) < 0) {
                displayScreen.setText("Error: Negative sqrt");
                return;
            }
            BigDecimal result = BigDecimal.valueOf(Math.sqrt(value.doubleValue()));
            currentExpression = result.stripTrailingZeros().toPlainString();
            displayScreen.setText(currentExpression);
            newInputExpected = true;
        } catch (Exception e) {
            displayScreen.setText("Error: " + e.getMessage());
            currentExpression = "";
            newInputExpected = true;
        }
    }

    private void handleCubeRoot() {
        try {
            BigDecimal value = currentExpression.isEmpty() ? BigDecimal.ZERO : new BigDecimal(currentExpression);
            BigDecimal result = BigDecimal.valueOf(Math.cbrt(value.doubleValue()));
            currentExpression = result.stripTrailingZeros().toPlainString();
            displayScreen.setText(currentExpression);
            newInputExpected = true;
        } catch (Exception e) {
            displayScreen.setText("Error: " + e.getMessage());
            currentExpression = "";
            newInputExpected = true;
        }
    }

    private void handleSquare() {
        try {
            BigDecimal value = currentExpression.isEmpty() ? BigDecimal.ZERO : new BigDecimal(currentExpression);
            BigDecimal result = value.pow(2, PRECISION);
            currentExpression = result.stripTrailingZeros().toPlainString();
            displayScreen.setText(currentExpression);
            newInputExpected = true;
        } catch (Exception e) {
            displayScreen.setText("Error: " + e.getMessage());
            currentExpression = "";
            newInputExpected = true;
        }
    }

    private void handleCube() {
        try {
            BigDecimal value = currentExpression.isEmpty() ? BigDecimal.ZERO : new BigDecimal(currentExpression);
            BigDecimal result = value.pow(3, PRECISION);
            currentExpression = result.stripTrailingZeros().toPlainString();
            displayScreen.setText(currentExpression);
            newInputExpected = true;
        } catch (Exception e) {
            displayScreen.setText("Error: " + e.getMessage());
            currentExpression = "";
            newInputExpected = true;
        }
    }

    private void handlePi() {
        currentExpression = PI.toPlainString();
        displayScreen.setText(currentExpression);
        newInputExpected = true;
    }

    private void handleLog() {
        try {
            BigDecimal value = currentExpression.isEmpty() ? BigDecimal.ZERO : new BigDecimal(currentExpression);
            if (value.compareTo(BigDecimal.ZERO) <= 0) {
                displayScreen.setText("Error: Log <= 0");
                return;
            }
            BigDecimal result = BigDecimal.valueOf(Math.log10(value.doubleValue()));
            currentExpression = result.stripTrailingZeros().toPlainString();
            displayScreen.setText(currentExpression);
            newInputExpected = true;
        } catch (Exception e) {
            displayScreen.setText("Error: " + e.getMessage());
            currentExpression = "";
            newInputExpected = true;
        }
    }

    private void handleCos() {
        try {
            BigDecimal value = currentExpression.isEmpty() ? BigDecimal.ZERO : new BigDecimal(currentExpression);
            BigDecimal result = BigDecimal.valueOf(Math.cos(value.doubleValue()));
            currentExpression = result.stripTrailingZeros().toPlainString();
            displayScreen.setText(currentExpression);
            newInputExpected = true;
        } catch (Exception e) {
            displayScreen.setText("Error: " + e.getMessage());
            currentExpression = "";
            newInputExpected = true;
        }
    }

    private void handleTan() {
        try {
            BigDecimal value = currentExpression.isEmpty() ? BigDecimal.ZERO : new BigDecimal(currentExpression);
            BigDecimal result = BigDecimal.valueOf(Math.tan(value.doubleValue()));
            currentExpression = result.stripTrailingZeros().toPlainString();
            displayScreen.setText(currentExpression);
            newInputExpected = true;
        } catch (Exception e) {
            displayScreen.setText("Error: " + e.getMessage());
            currentExpression = "";
            newInputExpected = true;
        }
    }

    // Basic calculator functions
    private void handleAC() {
        isReadOnly = true;
        displayScreen.setEditable(false);
        currentExpression = "";
        displayScreen.setText("Device Off!");
        newInputExpected = true;
    }

    private void handleMode() {
        isReadOnly = false;
        displayScreen.setEditable(true);
        displayScreen.setText(currentExpression.isEmpty() ? "0.0" : currentExpression);
        displayScreen.requestFocus();
    }

    private void handleDelete() {
        if (!isReadOnly && currentExpression.length() > 0) {
            currentExpression = currentExpression.substring(0, currentExpression.length() - 1);
            displayScreen.setText(currentExpression.isEmpty() ? "0.0" : currentExpression);
            newInputExpected = currentExpression.isEmpty();
        }
    }

    private void handleClear() {
        if (!isReadOnly) {
            currentExpression = "";
            displayScreen.setText("0.0");
            newInputExpected = true;
        }
    }

    private void handleDecimal() {
        if (!isReadOnly) {
            String[] parts = currentExpression.split("[+\\-*/]");
            if (parts.length == 0 || !parts[parts.length - 1].contains(".")) {
                appendToExpression(".");
            }
        }
    }

    private void appendToExpression(String value) {
        if (newInputExpected) {
            currentExpression = value;
            newInputExpected = false;
        } else {
            currentExpression += value;
        }
        displayScreen.setText(currentExpression);
    }

    private void appendOperator(String operator) {
        if (currentExpression.isEmpty() && !operator.equals("-")) {
            return;
        }
        
        if (!currentExpression.isEmpty() && 
            "+-*/".contains(currentExpression.substring(currentExpression.length() - 1))) {
            currentExpression = currentExpression.substring(0, currentExpression.length() - 1);
        }
        
        currentExpression += operator;
        displayScreen.setText(currentExpression);
        newInputExpected = false;
    }

    private void evaluateAndDisplay() {
        try {
            String expr = currentExpression
                .replace("×", "*")
                .replace("÷", "/")
                .replaceAll("[+\\-*/]+$", "");
            
            BigDecimal result = evaluateExpression(expr);
            currentExpression = result.stripTrailingZeros().toPlainString();
            displayScreen.setText(currentExpression);
            newInputExpected = true;
        } catch (Exception ex) {
            displayScreen.setText("Error: " + ex.getMessage());
            currentExpression = "";
            newInputExpected = true;
        }
    }

    private BigDecimal evaluateExpression(String expr) throws Exception {
        expr = expr.replaceAll("\\s+", "");
        Stack<BigDecimal> numbers = new Stack<>();
        Stack<Character> operators = new Stack<>();
        
        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);
            
            if (Character.isDigit(c) || c == '.') {
                StringBuilder numStr = new StringBuilder();
                while (i < expr.length() && (Character.isDigit(expr.charAt(i)) || expr.charAt(i) == '.')) {
                    numStr.append(expr.charAt(i++));
                }
                i--;
                
                try {
                    numbers.push(new BigDecimal(numStr.toString()));
                } catch (NumberFormatException e) {
                    throw new Exception("Invalid number format");
                }
            }
            else if (c == '+' || c == '-' || c == '*' || c == '/') {
                while (!operators.isEmpty() && hasPrecedence(c, operators.peek())) {
                    numbers.push(applyOperation(operators.pop(), numbers.pop(), numbers.pop()));
                }
                operators.push(c);
            } else {
                throw new Exception("Invalid character in expression");
            }
        }
        
        while (!operators.isEmpty()) {
            numbers.push(applyOperation(operators.pop(), numbers.pop(), numbers.pop()));
        }
        
        if (numbers.size() != 1) {
            throw new Exception("Invalid expression");
        }
        
        return numbers.pop();
    }
    
    private boolean hasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')') return false;
        if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-')) return false;
        return true;
    }
    
    private BigDecimal applyOperation(char op, BigDecimal b, BigDecimal a) throws Exception {
        switch (op) {
            case '+': return a.add(b, PRECISION);
            case '-': return a.subtract(b, PRECISION);
            case '*': return a.multiply(b, PRECISION);
            case '/': 
                if (b.compareTo(BigDecimal.ZERO) == 0) throw new Exception("Division by zero");
                return a.divide(b, PRECISION);
            default: throw new Exception("Unsupported operation");
        }
    }
}
