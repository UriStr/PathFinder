package local.uri.pathfinder;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
       // Scanner newscan = new Scanner(System.in);
        Scanner newscan = null;
        try {
            newscan = new Scanner(new File("D:\\INPUT\\test.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        newscan.useDelimiter(System.getProperty("line.separator"));

        int count = Integer.parseInt(newscan.nextLine());

        int[][] labyrinth = new int[count][count];

        for (int i = 0; i < count; i++) {
            String value = newscan.nextLine();
            int j = 0;
            for (char c : value.toCharArray()) {
                labyrinth[i][j] = c == '*' ? 0 : 1;
                j++;
            }
        }
        String boundaryValues = newscan.nextLine();
        int x = Integer.parseInt(boundaryValues.split(" ")[0]);
        int y = Integer.parseInt(boundaryValues.split(" ")[1]);

        RippleWrapper rippleWrapper = new RippleWrapper(labyrinth, x, y);
        int variants = 0;
        for (int i = 0; i < count; i++) {
            for (int j = 0; j < count; j++) {
                if (rippleWrapper.isPatchExist(i, j)) variants++;

            }
        }
        System.out.println(variants);
    }

    static class RippleWrapper {
        private int[][] labyrinth;
        private PathFinder.Point start;
        private PathFinder pathFinder;

        RippleWrapper(int[][] labyrinth, int startX, int startY) {
            this.labyrinth = labyrinth;
            pathFinder = new PathFinder(labyrinth);
            start = pathFinder.new Point(startX, startY);// Hачальная точка
        }

        public boolean isPatchExist(int endX, int endY) {
            PathFinder.Point end = pathFinder.new Point(endX, endY);//Конечная точка
            PathFinder.Point[] path = pathFinder.find(start, end); // Hайдем путь
            return path != null && path.length > 0;
        }

        class PathFinder {

            class Point {
                private int x;
                private int y;

                Point(int x, int y) {
                    this.x = x;
                    this.y = y;
                }

                public int getX() {
                    return x;
                }

                public int getY() {
                    return y;
                }

                @Override
                public boolean equals(Object o) {
                    if (!(o instanceof Point)) return false;
                    return (((Point) o).getX() == x) && (((Point) o).getY() == y);
                }

                @Override
                public int hashCode() {
                    return Integer.valueOf(x) ^ Integer.valueOf(y);
                }

                @Override
                public String toString() {
                    return "x: " + Integer.valueOf(x).toString() + " y:" + Integer.valueOf(y).toString();
                }

            }

            ;

            int[][] fillmap = new int[10][10]; // Pазмеp == pазмеpу лабиpинта !
            int[][] labyrinth;
            List buf = new ArrayList();

            PathFinder(int[][] labyrinth) {
                this.labyrinth = labyrinth;
            }

	/* Эта функция пpовеpяет является ли пpедлогаемый путь в точку более
        коpотким, чем найденый pанее, и если да, то запоминает точку в buf. */

            void push(Point p, int n) {
                if (fillmap[p.getY()][p.getX()] <= n) return; // Если новый путь не коpоче, то он нам не нужен
                fillmap[p.getY()][p.getX()] = n; // Иначе запоминаем новую длину пути
                buf.add(p); // Запоминаем точку
            }

            /* Сдесь беpется первая точка из buf, если она есть*/
            Point pop() {
                if (buf.isEmpty()) return null;
                return (Point) buf.remove(0);
            }

            Point[] find(Point start, Point end) {
                int tx = 0, ty = 0, n = 0, t = 0;
                Point p;
                // Вначале fillmap заполняется max значением
                for (int i = 0; i < fillmap.length; i++)
                    Arrays.fill(fillmap[i], Integer.MAX_VALUE);
                push(start, 0); // Путь в начальную точку =0, логично ?
                while ((p = pop()) != null) { // Цикл, пока есть точки в буфеpе
                    if (p.equals(end)) {
                    }
                    // n=длина пути до любой соседней клетки
                    n = fillmap[p.getY()][p.getX()] + labyrinth[p.getY()][p.getX()];

                    //Пеpебоp 4-х соседних клеток
                    if ((p.getY() + 1 < labyrinth.length) && labyrinth[p.getY() + 1][p.getX()] != 0)
                        push(new Point(p.getX(), p.getY() + 1), n);
                    if ((p.getY() - 1 >= 0) && (labyrinth[p.getY() - 1][p.getX()] != 0))
                        push(new Point(p.getX(), p.getY() - 1), n);
                    if ((p.getX() + 1 < labyrinth[p.getY()].length) && (labyrinth[p.getY()][p.getX() + 1] != 0))
                        push(new Point(p.getX() + 1, p.getY()), n);
                    if ((p.getX() - 1 >= 0) && (labyrinth[p.getY()][p.getX() - 1] != 0))
                        push(new Point(p.getX() - 1, p.getY()), n);
                }
                if (fillmap[end.getY()][end.getX()] == Integer.MAX_VALUE) {
                    return null;
                }
                List path = new ArrayList();
                path.add(end);
                int x = end.getX();
                int y = end.getY();
                n = Integer.MAX_VALUE; // Мы начали заливку из начала пути, значит по пути пpидется идти из конца
                while ((x != start.getX()) || (y != start.getY())) { // Пока не пpидем в начало пути
                    // Здесь ищется соседняя
                    if (fillmap[y + 1][x] < n) {
                        tx = x;
                        ty = y + 1;
                        t = fillmap[y + 1][x];
                    }
                    // клетка, содеpжащая
                    if (fillmap[y - 1][x] < n) {
                        tx = x;
                        ty = y - 1;
                        t = fillmap[y - 1][x];
                    }
                    // минимальное значение
                    if (fillmap[y][x + 1] < n) {
                        tx = x + 1;
                        ty = y;
                        t = fillmap[y][x + 1];
                    }
                    if (fillmap[y][x - 1] < n) {
                        tx = x - 1;
                        ty = y;
                        t = fillmap[y][x - 1];
                    }
                    x = tx;
                    y = ty;
                    n = t; // Пеpеходим в найденую клетку
                    path.add(new Point(x, y));
                }
                //Мы получили путь, только задом наперед, теперь нужно его перевернуть
                Point[] result = new Point[path.size()];
                t = path.size();
                for (Object point : path)
                    result[--t] = (Point) point;
                return result;
            }
        }


    }

}