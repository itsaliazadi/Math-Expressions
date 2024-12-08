import java.util.Stack;
import java.util.ArrayList;
import java.util.Scanner;
import java.lang.Math;

public class ExpressionCalculator {
    private Stack<String> expressionStack = new Stack<>();

    ExpressionCalculator() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the expression in infix notation:");

        String userInput = scanner.nextLine();
        while (!userInput.matches(("-?\\d+")) && !userInput.equals("x") && !userInput.equals(")") && !userInput.equals("(")) {
            System.out.println("Infix expression should start with a number or variable. Please enter your input again:");
            userInput = scanner.nextLine();
        }

        int openParenthesesCount = 0;
        boolean isPreviousDigit = false;

        while (!userInput.equals("q")) {
            if ((userInput.matches("-?\\d+") && !isPreviousDigit) || 
                (userInput.matches("[+\\-^*/]") && isPreviousDigit) || 
                (userInput.equals("x") && !isPreviousDigit)) {
                expressionStack.push(userInput);
                System.out.println("Enter the next element or q to exit:");
                isPreviousDigit = !isPreviousDigit;
            } else if (userInput.equals("(")) {
                expressionStack.push(userInput);
                openParenthesesCount++;
                System.out.println("Enter the next element or q to exit:");
            } else if (userInput.equals(")")) {
                if (openParenthesesCount > 0) {
                    expressionStack.push(userInput);
                    openParenthesesCount--;
                } else {
                    System.out.println("No open parentheses to close.");
                }
                System.out.println("Enter the next element or q to exit:");
            } else if (userInput.matches("-?\\d+") && isPreviousDigit) {
                System.out.println("After a number or closing parentheses or variable, there must be an operator. Please enter the element again or END to exit:");
            } else if (userInput.matches("[+\\-^*/]") && !isPreviousDigit) {
                System.out.println("After an operator or opening parentheses, there must be a number or variable. Please enter the element again or END to exit:");
            } else {
                System.out.println("Invalid input. Press q if the expression is finished, otherwise continue:");
            }

            userInput = scanner.nextLine();
        }

        if (!isPreviousDigit) {
            System.out.println("There was no operand after the last operator, so it was removed.");
            expressionStack.pop();
        }
        
        if (openParenthesesCount > 0) {
            for (int i = 0; i < openParenthesesCount; i++) {
                expressionStack.push(")");
            }
        }
        System.out.println("The expression is: " + expressionStack);
    }

    public Stack<String> convertToPostfix() {
        Stack<String> postfixStack = new Stack<>();
        Stack<String> operatorStack = new Stack<>();
        Stack<String> expressionCopy = new Stack<>();
        expressionCopy.addAll(expressionStack);

        while (!expressionCopy.isEmpty()) {
            String token = expressionCopy.remove(0);
            if (token.matches("-?\\d+|^x$")) {
                postfixStack.push(token);
            } else if (token.equals("(")) {
                operatorStack.push(token);
            } else if (token.equals(")")) {
                while (!operatorStack.isEmpty() && !operatorStack.peek().equals("(")) {
                    postfixStack.push(operatorStack.pop());
                }
                operatorStack.pop();
            } else {
                while (!operatorStack.isEmpty() && precedence(token) <= precedence(operatorStack.peek())) {
                    postfixStack.push(operatorStack.pop());
                }
                operatorStack.push(token);
            }
        }

        while (!operatorStack.isEmpty()) {
            postfixStack.push(operatorStack.pop());
        }

        return postfixStack;
    }

    public Stack<String> convertToPrefix() {
        Stack<String> operatorStack = new Stack<>();
        Stack<String> operandStack = new Stack<>();
        Stack<String> expressionCopy = new Stack<>();
        expressionCopy.addAll(expressionStack);

        while (!expressionCopy.isEmpty()) {
            String token = expressionCopy.pop();
            if (token.matches("-?\\d+|^x$")) {
                operandStack.push(token);
            } else if (token.equals(")")) {
                operatorStack.push(token);
            } else if (token.equals("(")) {
                while (!operatorStack.isEmpty() && !operatorStack.peek().equals(")")) {
                    String operator = operatorStack.pop();
                    String operand1 = operandStack.pop();
                    String operand2 = operandStack.pop();
                    String prefix = operator + " " + operand1 + " " + operand2;
                    operandStack.push(prefix);
                }
                operatorStack.pop();
            } else {
                while (!operatorStack.isEmpty() && precedence(token) < precedence(operatorStack.peek())) {
                    String operator = operatorStack.pop();
                    String operand1 = operandStack.pop();
                    String operand2 = operandStack.pop();
                    String prefix = operator + " " + operand1 + " " + operand2;
                    operandStack.push(prefix);
                }
                operatorStack.push(token);
            }
        }

        while (!operatorStack.isEmpty()) {
            String operator = operatorStack.pop();
            String operand1 = operandStack.pop();
            String operand2 = operandStack.pop();
            String prefix = operator + " " + operand1 + " " + operand2;
            operandStack.push(prefix);
        }

        Stack<String> resultStack = new Stack<>();
        for (String item : operandStack.pop().split(" ")) {
            resultStack.push(item);
        }

        return resultStack;
    }

    public double calculatePrefix(double value){
        Stack<String> prefix = convertToPrefix();
        Stack<Double> operands = new Stack<>();
        while (!prefix.empty()){
            String token = prefix.pop();
            if (token.matches(("-?\\d+(\\.\\d+)?"))){
                operands.push(Double.parseDouble(token));
            }
            else if(token.equals("x")){
                operands.push(value);
            }
            else if (token.matches("[+\\-*/^]")) {
                double operand1 = operands.pop();
                double operand2 = operands.pop();
                switch (token) {    
                    case "+":
                        operands.push(operand1 + operand2);
                        break;
                    case "-":
                        operands.push(operand1 - operand2);
                        break;
                    case "*":
                        operands.push(operand1 * operand2);
                        break;
                    case "/":
                        operands.push((double)operand1 / operand2);
                        break;
                    case "^":
                        operands.push(Math.pow(operand1, operand2));
                        break;
                    default:
                        break;
                }

            }
        }
        return operands.pop();
    }


    public double calculatePostfix(){
        Stack<String> postfix = convertToPostfix();
        Stack<Double> operands = new Stack<>();
        while (!postfix.empty()){
            String token = postfix.remove(0);
            if (token.matches(("-?\\d+(\\.\\d+)?"))){
                operands.push(Double.parseDouble(token));
            }
            else if (token.matches("[+\\-*/^]")) {
                double operand1 = operands.pop();
                double operand2 = operands.pop();
                switch (token) {    
                    case "+":
                        operands.push(operand1 + operand2);
                        break;
                    case "-":
                        operands.push(operand2 - operand1);
                        break;
                    case "*":
                        operands.push(operand1 * operand2);
                        break;
                    case "/":
                        operands.push((double)operand2 / operand1);
                        break;
                    case "^":
                        operands.push(Math.pow(operand2, operand1));
                        break;
                    default:
                        break;
                }
            }
        }
        return operands.pop();
    }


    int precedence(String c) {
        if (c.equals("^"))
            return 3;
        else if (c.equals("/") || c.equals( "*"))
            return 2;
        else if (c.equals("+") || c.equals("-"))
            return 1;
        else
            return -1;
    }

    void graph(){
        ArrayList<ArrayList<Double>> points = new ArrayList<>();
        for(double i = -100; i <= 100; i = i + 0.1){
            ArrayList<Double> temp = new ArrayList<>();
            temp.add(i);
            temp.add(calculatePrefix(i));
            points.add(temp);
        }
        draw d = new draw(points);
    }

    public static void main(String[] args){

        ExpressionCalculator a = new ExpressionCalculator();
        System.out.println(a.convertToPostfix());
        System.out.println(a.convertToPrefix());
        a.graph();

    }
}