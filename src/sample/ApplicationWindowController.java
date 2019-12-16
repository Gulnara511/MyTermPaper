package sample;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import sample.animation.Population;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ApplicationWindowController extends HTML
{
    private ObservableList<Population> population = FXCollections.observableArrayList();
    @FXML
    private ComboBox period;
    @FXML
    private ComboBox type;
    @FXML
    private ColorPicker color;
    @FXML
    private TableView<Population> tablePopulation;
    @FXML
    private TableColumn yearColumn;
    @FXML
    private TableColumn quantityColumn;
    @FXML
    LineChart<Number, Number> fxChart;
    @FXML
    NumberAxis xAxis;
    @FXML
    NumberAxis yAxis;
    @FXML
    private Button graf;
    @FXML
    private Button table;
    @FXML
    private TextArea data;
    @FXML
     Label USER;
    @FXML
     Button SAVE;
    @FXML
    void initialize() throws SQLException, ClassNotFoundException, IOException {
        graf.setOnAction((event) -> {
            ArrayList<Double> gorizontal = new ArrayList<>();
            ArrayList<Double> vertical = new ArrayList<>();

            fxChart.getData().clear();

            Color c = color.getValue();
            String color_style = c.toString().substring(2, 8);
            XYChart.Series<Number, Number> series1 = new XYChart.Series<>();

            ResultSet resSet = null;
            String select = " SELECT * FROM " + Const.POPULATION_TABLE;
            try {

                PreparedStatement prSt = getDbConnection().prepareStatement(select);
                resSet = prSt.executeQuery();
                while (resSet.next()&&!resSet.getString(1).equals(period.getValue())) {
                    series1.getData().add(new XYChart.Data<>(Integer.parseInt(resSet.getString(1)), Integer.parseInt(resSet.getString(2).replaceAll(" ", ""))));
                    gorizontal.add(Double.parseDouble(resSet.getString(1)));
                    vertical.add(Double.parseDouble(resSet.getString(2).replaceAll(" ", "")));
                }

                System.out.println("Значения по горизонтали" + gorizontal);
                System.out.println("Значения по вертикали" + vertical);
                xAxis.setLowerBound(1890);
                xAxis.setUpperBound(Double.parseDouble(String.valueOf(period.getValue())));
                xAxis.setAutoRanging(false);
                xAxis.setTickUnit(10);
                data.appendText("Выбран диапозон до " + period.getValue() + " года");
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            fxChart.getData().add(series1);
            Platform.runLater(() -> {
                Node node = fxChart.lookup(".default-color0.chart-series-line");
                node.setStyle("-fx-stroke: #" + color_style + ";");
                Node legend = fxChart.lookup(".default-color0.chart-legend-item-symbol");
                if (legend != null) {
                legend.setStyle("-fx-background-color: #" + color_style + ", white;");}
                for (int i = 0; i < series1.getData().size(); i++) {
                    XYChart.Data<Number, Number> round = series1.getData().get(i);
                    Node roundColor = round.getNode().lookup(".chart-line-symbol");
                    roundColor.setStyle("-fx-background-color: #" + color_style + "; -fx-background-radius: 0; -fx-padding: 1px;");
                }

            });
            fxChart.setLegendVisible(false);

            series1.setName("Численность населения России");
            if (type.getValue().equals("Линейная")) {
                data.appendText(" Построена линейная аппроксимация, его ошибка составляет: " + line(gorizontal, vertical, fxChart) + "\n");
            } else if (type.getValue().equals("Квадратичная")) {
                data.appendText(" Построена квадратичная аппроксимация, его ошибка составляет: " + quadratic(gorizontal, vertical, fxChart) + "\n");
            } else if (type.getValue().equals("Логарифмическая")) {
                data.appendText(" Построена логарифмическая аппроксимация, его ошибка составляет: " + logarifm(gorizontal, vertical, fxChart) + "\n");
            } else if (type.getValue().equals("Экспоненциальная")) {
                data.appendText(" Построена экспоненциальная аппроксимация, его ошибка составляет: " + exponenta(gorizontal, vertical, fxChart) + "\n");
            }
            data.appendText(" Выбран цвет для графика: " + color_style + "\n");
        });

        table.setOnAction((event) -> {
            try {
                initData();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            // устанавливаем тип и значение которое должно хранится в колонке
            yearColumn.setCellValueFactory(new PropertyValueFactory<Population, String>("year"));
            quantityColumn.setCellValueFactory(new PropertyValueFactory<Population, String>("quantity"));

            // заполняем таблицу данными
            tablePopulation.setItems(population);
            data.appendText("  Выведены данные в таблицу\n");
        });

        SAVE.setOnAction(event -> {
            String insert = " INSERT INTO " + Const.SAVE_TABLE + " (" + Const.SAVE_LOGIN + "," + Const.SAVE_ACTION + "," + Const.SAVE_TIME + ") " + " VALUES(?,?,?) ";
            PreparedStatement prSt = null;
            try {
                prSt = getDbConnection().prepareStatement(insert);
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            try {
                prSt.setString(1, USER.getText());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                prSt.setString(2, data.getText());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                prSt.setString(3, String.valueOf(LocalDateTime.now()));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                prSt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static double line(ArrayList<Double> x, ArrayList<Double> y, LineChart fxChart) {
        double n = x.size();
        double sumXSquare = 0;
        double sumX = 0;
        double sumY = 0;
        double sumXY = 0;
        double Aa = 0;
        for (int i = 0; i < x.size(); i++) {
            sumX += x.get(i);
            sumY += y.get(i);
            sumXSquare += Math.pow(x.get(i), 2);
            sumXY += x.get(i) * y.get(i);
        }
        System.out.println(sumXY);
        double a = (sumX * sumY - n * sumXY) / (sumX * sumX - sumXSquare * n);
        double b = (sumX * sumXY - sumXSquare * sumY) / (sumX * sumX - sumXSquare * n);
        System.out.println("Значение а: " + a + ", значение b: " + b);
        XYChart.Series<Double, Double> series2 = new XYChart.Series<>();
        series2.getData().add(new XYChart.Data(x.get(0), (a * x.get(0) + b)));
        series2.getData().add(new XYChart.Data((x.get(x.size() - 1)), (a * x.get(x.size() - 1) + b)));
        fxChart.getData().add(series2);
        //Ошибка аппроксимации
        for (int i = 0; i < x.size(); i++) {
            Aa += Math.abs((y.get(i)-(a*x.get(i)+b))/y.get(i));
        }

        double A = (1/n)*Aa*100;
        System.out.println("Средняя ошибка аппроксимации: " + A);
        return A;
    }

    public static double quadratic(ArrayList<Double> x, ArrayList<Double> y, LineChart fxChart) {
        double n = x.size();
        double sumY = 0;
        double sumX = 0;
        double sumXSquare = 0;
        double sumXCube = 0;
        double sumXFourth = 0;
        double sumXY = 0;
        double sumXSquareY = 0;
        double Aa = 0;

        for (int i = 0; i < x.size(); i++) {
            sumX += x.get(i);
            sumY += y.get(i);
            sumXSquare += Math.pow(x.get(i), 2);
            sumXCube += Math.pow(x.get(i), 3);
            sumXFourth += Math.pow(x.get(i), 4);
            sumXSquareY += Math.pow(x.get(i), 2) * y.get(i);
            sumXY += x.get(i) * y.get(i);
        }
        double[][] kr = new double[3][3];
        kr[0][0] = sumXSquare;
        kr[0][1] = sumX;
        kr[0][2] = n;
        kr[1][0] = sumXCube;
        kr[1][1] = sumXSquare;
        kr[1][2] = sumX;
        kr[2][0] = sumXFourth;
        kr[2][1] = sumXCube;
        kr[2][2] = sumXSquare;
        double[] B = new double[3];
        B[0] = sumY;
        B[1] = sumXY;
        B[2] = sumXSquareY;
        System.out.println("x = " + sumX);
        System.out.println("y = " + sumY);
        System.out.println("x2 = " + sumXSquare);
        System.out.println("x3 = " + sumXCube);
        System.out.println("x4 = " + sumXFourth);
        System.out.println("xy = " + sumXY);
        System.out.println("x2y = " + sumXSquareY);
        double det = kr[0][0] * kr[1][1] * kr[2][2]
                + kr[0][1] * kr[1][2] * kr[2][0]
                + kr[1][0] * kr[2][1] * kr[0][2]
                - kr[2][0] * kr[1][1] * kr[0][2]
                - kr[1][0] * kr[0][1] * kr[2][2]
                - kr[2][1] * kr[1][2] * kr[0][0];

        double aDet = B[0] * kr[1][1] * kr[2][2] +
                kr[0][1] * kr[1][2] * B[2] +
                B[1] * kr[2][1] * kr[0][2] -
                B[2] * kr[1][1] * kr[0][2] -
                B[1] * kr[0][1] * kr[2][2] -
                kr[2][1] * kr[1][2] * B[0];

        double bDet = kr[0][0] * B[1] * kr[2][2] +
                B[0] * kr[1][2] * kr[2][0] +
                kr[1][0] * B[2] * kr[0][2] -
                kr[2][0] * B[1] * kr[0][2] -
                kr[1][0] * B[0] * kr[2][2] -
                B[2] * kr[1][2] * kr[0][0] ;

        double cDet = kr[0][0] * kr[1][1] * B[2] +
                kr[0][1] * B[1] * kr[2][0] +
                kr[1][0] * kr[2][1] * B[0] -
                kr[2][0] * kr[1][1] * B[0] -
                kr[1][0] * kr[0][1] * B[2] -
                B[1] * kr[1][2] * kr[0][0];

        System.out.println("Главный определитель " + det);
        System.out.println("а определитель " + aDet);
        System.out.println("в определитель " + bDet);
        System.out.println("с определитель " + cDet);
        double a = aDet / det;
        double b = bDet / det;
        double c = cDet / det;
        System.out.println("a = " + a);
        System.out.println("b = " + b);
        System.out.println("c = " + c);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.print(kr[i][j] + " ");
            }
            System.out.println();
        }
        XYChart.Series<Double, Double> series3 = new XYChart.Series<>();

        for (int i = 5; i < x.size()-5; i++) {
            series3.getData().add(new XYChart.Data(x.get(i), a * x.get(i) * x.get(i) + b * x.get(i) + c));
        }
        fxChart.getData().add(series3);

        //Ошибка аппроксимации
        for (int i = 0; i < x.size(); i++) {
            Aa += Math.abs((y.get(i)-(a * x.get(i) * x.get(i) + b * x.get(i) + c))/y.get(i));
        }

        double A = (1/n)*Aa*100;
        System.out.println("Средняя ошибка аппроксимации: " + A);
        return A;

//        XYChart.Series<Double, Double> series4 = new XYChart.Series<>();
//        series4.getData().add(new XYChart.Data(1900, 60000000));
//        fxChart.getData().add(series4);
//        fxChart.getData().remove(series4);
    }

    public static double logarifm(ArrayList<Double> x, ArrayList<Double> y, LineChart fxChart) {
        double n = x.size();
        double sumY = 0;
        double sumX = 0;
        double sumLnX = 0;
        double sumLnSquareX = 0;
        double sumYlnX = 0;
        double Aa = 0;

        for (int i = 0; i < x.size(); i++) {
            sumX += x.get(i);
            sumY += y.get(i);
            sumLnX += Math.log(x.get(i));
            sumLnSquareX += Math.pow(Math.log(x.get(i)), 2);
            sumYlnX += y.get(i) * Math.log(x.get(i));
        }
        System.out.println("x = " + sumX + ", y = " + sumY + ", ln(x) = " + sumLnX + ", ln(x)^2 = " + sumLnSquareX + ", y*ln(x) =" + sumYlnX);
        double b = (n * sumYlnX - sumLnX * sumY) / (n * sumLnSquareX - sumLnX * sumLnX);
        double a = (1 / n) * sumY - (b / n) * sumLnX;
        System.out.println(b + "   " + a);
        XYChart.Series<Double, Double> series3 = new XYChart.Series<>();
        for (int i = 0; i < x.size(); i++) {
            series3.getData().add(new XYChart.Data(x.get(i), a + b * Math.log(x.get(i))));
        }
        fxChart.getData().add(series3);

        //Ошибка аппроксимации
        for (int i = 0; i < x.size(); i++) {
            Aa += Math.abs((y.get(i)-(a+b*Math.log(x.get(i))))/y.get(i));
        }

        double A = (1/n)*Aa*100;
        System.out.println("Средняя ошибка аппроксимации: " + A);
        return A;
    }

    public static double exponenta(ArrayList<Double> x, ArrayList<Double> y, LineChart fxChart) {
        double n = x.size();
        double sumY = 0;
        double sumX = 0;
        double sumXSquare = 0;
        double sumLnY = 0;
        double sumXlnY = 0;
        double Aa = 0;

        for (int i = 0; i < x.size(); i++) {
            sumX += x.get(i);
            sumY += y.get(i);
            sumXSquare += Math.pow(x.get(i), 2);
            sumLnY += Math.log(y.get(i));
            sumXlnY += x.get(i) * Math.log(y.get(i));
        }
        double b = (n * sumXlnY - sumX * sumLnY) / (n * sumXSquare - sumX * sumX);
        double a = sumLnY / n - (b / n) * sumX;
        System.out.println(a + " " + b);
        System.out.println("y = " + sumY + ", x = " + sumX + ", x*x = " + sumXSquare + ", ln(y) = " + sumLnY + ", x*ln(y) " + sumXlnY);
        XYChart.Series<Double, Double> series3 = new XYChart.Series<>();
        for (int i = 0; i < x.size(); i++) {
            series3.getData().add(new XYChart.Data(x.get(i), Math.exp(a + b * x.get(i))));
        }
        fxChart.getData().add(series3);
        //Ошибка аппроксимации
        for (int i = 0; i < x.size(); i++) {
            Aa += Math.abs((y.get(i)-(Math.exp(a + b * x.get(i)))))/y.get(i);
        }

        double A = (1/n)*Aa*100;
        System.out.println("Средняя ошибка аппроксимации: " + A);
        return A;
    }

    private ArrayList<String> initData() throws SQLException, IOException, ClassNotFoundException {
        ArrayList<String> XY = bd();
        ArrayList<String> X = new ArrayList<>();
        ArrayList<String> Y = new ArrayList<>();
        for (int i = 2; i < XY.size(); i = i + 2) {
            X.add(XY.get(i));
        }
        System.out.println(X.size());
        for (int i = 3; i < XY.size(); i = i + 2) {
            Y.add(XY.get(i));
        }
        for (int i = 0; i < X.size(); i++) {
            population.add(new Population(X.get(i), Y.get(i)));
        }
        return XY;
    }

    public void  usersLogin (String text) {
        USER.setText(text);
    }
}
