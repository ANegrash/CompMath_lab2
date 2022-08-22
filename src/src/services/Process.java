package src.services;

import org.jetbrains.annotations.NotNull;
import src.methods.Iteration;
import src.methods.Secant;
import src.methods.Tangent;
import src.objects.AnswerX;
import src.objects.EqSystem;
import src.objects.Func;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class Process {
    public Process() {
        startMessage();
        process();
        exitMessage();
    }

    private void process() {
        while (true) {
            String command = prompt(">");
            if (command.equals("q")) break;

            switch (command) {
                case "sing"     -> singular();
                case "syst"     -> multiple();
                case "h"        -> help();
                default         -> err();
            }
        }
    }

    private void startMessage() {
        System.out.println("""
                Лабораторная №2: "Системы нелинейных уравнений"
                Вариант: 2бв
                Автор: Неграш А.В., P3230""");
        help();
    }

    private void exitMessage() {
        printLine();
        System.out.println("""
                Спасибо за использование решателя, встретимся позже.
                "Встретимся ведь?..""");
    }

    private void printLine() {
        System.out.println();
    }

    private void help() {
        printLine();
        System.out.println("""
            Список доступных команд:
            sing  - решить нелинейное уравнение
            syst  - решить систему нелинейных уравнений
            q     - выйти из программы
            h     - показать список доступных команд""");
        printLine();
    }

    private void singular() {
        printLine();
        System.out.println("""
            Выберите уравнение:
            1: x + cos(x) - 0.67x^3 - 1 = 0
            2: x^3 + 2x^2 - 5x - 5 = 0
            3: -x^2 + 5 = 0""");
        printLine();
        int eq_id = getId(3);

        Func function = switch (eq_id) {
            case 1 -> new Func() {
                @Override
                public double calcFunc(double x) {
                    return x + Math.cos(x) - 0.67 * Math.pow(x, 3) - 1;
                }
                @Override
                public double calcDer(double x) {
                    return 1 - Math.sin(x) - 0.67 * 3 * Math.pow(x, 2);
                }
            };
            case 2 -> new Func() {
                @Override
                public double calcFunc(double x) {
                    return Math.pow(x, 3) + 2 * Math.pow(x, 2) - 5 * x - 5;
                }
                @Override
                public double calcDer(double x) {
                    return 3 * Math.pow(x, 2) + 4 * x - 5;
                }
            };
            default -> new Func() {
                @Override
                public double calcFunc(double x) {
                    return -Math.pow(x, 2) + 5;
                }
                @Override
                public double calcDer(double x) {
                    return -2 * x;
                }
            };
        };

        System.out.println("Input precision:");
        double precision = getFloat();
        System.out.println("Input the initial approximation for Newton's method:");
        double initApprox = getFloat();

        double begin;
        double end;

        while (true) {
            System.out.println("Input the beginning of the interval for the Secant method:");
            begin = getFloat();
            System.out.println("Input the end of the interval for the Secant method:");
            end = getFloat();

            if (function.calcFunc(begin) * function.calcFunc(end) > 0)
                System.out.println("The interval contains either one or multiple solutions.");
            else break;
        }

        printLine();
        System.out.println("Starting Newton's method...");
        AnswerX newtSolution = new Tangent().calculate(function, precision, initApprox, 1);
        System.out.println("The solution is: " + newtSolution.toString());
        printLine();
        System.out.println("Starting the Secant method...");
        AnswerX secSolution = new Secant().calculate(function, precision, begin, end, 1);
        System.out.println("The solution is: " + secSolution.toString());
        printLine();

        System.out.println("The difference between the two solutions is: " +
                FormatterToDouble.format(Math.abs(newtSolution.x - secSolution.x)));
    }

    private void multiple() {
        printLine();
        System.out.println("""
            Choose an equation system to solve:
            1: y = x^3;
               y = x^2 - 6.
               
            2: y = 0.1x^3;
               y = x^2 - 0.5.""");
        printLine();
        int eq_id = getId(2);

        EqSystem system = switch (eq_id) {
            case 1 -> new EqSystem() {
                @Override
                public double x1(double y) {
                    return Math.cbrt(y);
                }
                @Override
                public double y2(double x) {
                    return Math.pow(x, 2) - 6;
                }
            };
            default -> new EqSystem() {
                @Override
                public double x1(double y) {
                    return Math.cbrt(10*y);
                }
                @Override
                public double y2(double x) {
                    return Math.pow(x, 2) - 0.5;
                }
            };
        };

        System.out.println("Input precision:");
        double precision = getFloat();
        System.out.println("Input the initial approximation for x:");
        double x = getFloat();
        System.out.println("Input the initial approximation for y:");
        double y = getFloat();

        printLine();
        System.out.println("Starting the simple iteration method...");
        System.out.println("The solution is: " +
                new Iteration().calculate(system, precision, x, y, 1).toString()
        );
    }

    private int getId(int upperLimit) {
        int ret = 0;
        while (true) {
            String temp = prompt(">");

            try { ret = Integer.parseInt(temp); }
            catch (NumberFormatException ignore) {}

            if (ret >= 1 && ret <= upperLimit) return ret;
            System.out.println("Incorrect value, try again");
        }
    }

    private double getFloat() {
        while (true) {
            String temp = prompt(">");

            try { return Double.parseDouble(temp); }
            catch (NumberFormatException e) { System.out.println("Incorrect value, try again"); }
        }
    }

    private void err() {
        System.out.println("Incorrect command. To see the list of commands, type \"h\".");
    }

    private String prompt(String prompt) {
        System.out.print(prompt);
        return readFromScanner(new Scanner(System.in));
    }

    private String readFromScanner(@NotNull Scanner scanner) {
        try { return scanner.nextLine(); }
        catch (NoSuchElementException e) { return "q"; }
    }
}
