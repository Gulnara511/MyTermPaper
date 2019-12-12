package sample;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

public class HTML extends DatabaseHandler {
    ArrayList<String> x = new ArrayList<>();
    ArrayList<String> y = new ArrayList<>();


    public ArrayList<String> bd() throws IOException, SQLException, ClassNotFoundException {
        ArrayList xy = new ArrayList();
        String truncate = " TRUNCATE " + Const.POPULATION_TABLE;
        PreparedStatement prSt2 = getDbConnection().prepareStatement(truncate);
        prSt2.execute();
        String url = "https://worldtable.info/gosudarstvo/chislennost-naselenija-rossii-po-godam-v-odno.html";
        Document doc1 = Jsoup.connect(url) // коннектимся к сайту
                .userAgent("Mozilla")       // сообщаем сайту что мы - браузер
                .get();                     // забираем страницу целиком
        String title = doc1.title();        // получаем название страницы
        System.out.println(title);
        System.out.println("");
        Elements sheets = doc1.getElementsByTag("table"); // получаем все элементы по тегу "table"
        Elements mySheets = sheets.select("table[class=\"\"]"); // выбираем "table" только с определенным
        // названием класса. такую выборку можно было сделать ещё на строке № 7 (из объекта типа Document).
        // здесь просто показаны разные варианты.
        String insert = " INSERT INTO " + Const.POPULATION_TABLE + " (" + Const.POPULATION_YEAR + "," + Const.POPULATION_QUANTITY + ") " + " VALUES(?,?) ";
        PreparedStatement prSt = getDbConnection().prepareStatement(insert);

        for (Element sheet : sheets) { // Elements можно перебирать в циклах
//            System.out.println("\n" + sheet.className()); // выводим имя класса элемента (в нашем случае - имя класса таблицы(NB! это не java класс!)
            Elements rows = sheet.select("tr"); // выбираем все элементы по горизонталям(строкам таблицы) и складируем их в новый объект типа Elements
            for (Element row : rows) {
                Elements words = row.getElementsByTag("td"); // складируем отдельные ячейки в новый объект типа Elements
                // выводим ячейки таблицы в консоль
//                System.out.printf("%-32s", words.get(0).text()); // отформатировано на скорую руку, под первую таблицу, сорри
//                System.out.printf("%-32s", words.get(1).text());

                xy.add(words.get(0).text());
                xy.add(words.get(1).text());
                prSt.setString(1, words.get(0).text());
                prSt.setString(2, words.get(1).text());

                prSt.execute();
//                System.out.println();
            }
        }
        System.out.println("Первый элемент массива: " + xy.get(2));
        System.out.println("Второй элемент массива: " + xy.get(3));
        String delete = " DELETE FROM " + Const.POPULATION_TABLE + " WHERE QUANTITY='Численность населения'";
        PreparedStatement prSt1 = getDbConnection().prepareStatement(delete);
        prSt1.execute();
        return xy;
    }

}


