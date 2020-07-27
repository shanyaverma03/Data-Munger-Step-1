package com.stackroute.datamunger;

import java.util.ArrayList;

/*There are total 5 DataMungertest files:
 *
 * 1)DataMungerTestTask1.java file is for testing following 3 methods
 * a)getSplitStrings()  b) getFileName()  c) getBaseQuery()
 *
 * Once you implement the above 3 methods,run DataMungerTestTask1.java
 *
 * 2)DataMungerTestTask2.java file is for testing following 3 methods
 * a)getFields() b) getConditionsPartQuery() c) getConditions()
 *
 * Once you implement the above 3 methods,run DataMungerTestTask2.java
 *
 * 3)DataMungerTestTask3.java file is for testing following 2 methods
 * a)getLogicalOperators() b) getOrderByFields()
 *
 * Once you implement the above 2 methods,run DataMungerTestTask3.java
 *
 * 4)DataMungerTestTask4.java file is for testing following 2 methods
 * a)getGroupByFields()  b) getAggregateFunctions()
 *
 * Once you implement the above 2 methods,run DataMungerTestTask4.java
 *
 * Once you implement all the methods run DataMungerTest.java.This test case consist of all
 * the test cases together.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataMunger {

    /*
     * This method will split the query string based on space into an array of words
     * and display it on console
     */

    public String[] getSplitStrings(String queryString) {

        return queryString.toLowerCase().replaceAll("\\s", " ").split(" ");
    }

    /*
     * Extract the name of the file from the query. File name can be found after a
     * space after "from" clause. Note: ----- CSV file can contain a field that
     * contains from as a part of the column name. For eg: from_date,from_hrs etc.
     *
     * Please consider this while extracting the file name in this method.
     */

    public String getFileName(String queryString) {

        StringBuilder name = new StringBuilder();
        Pattern pattern = Pattern.compile("(from\\s)(\\w+.csv)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(queryString);
        if (matcher.find()) {
            name.append(matcher.group(2));
        }
        return name.toString();
    }

    /*
     * This method is used to extract the baseQuery from the query string. BaseQuery
     * contains from the beginning of the query till the where clause
     *
     * Note: ------- 1. The query might not contain where clause but contain order
     * by or group by clause 2. The query might not contain where, order by or group
     * by clause 3. The query might not contain where, but can contain both group by
     * and order by clause
     */

    public String getBaseQuery(String queryString) {

        StringBuilder base = new StringBuilder();
        if (queryString == null) {
            base = null;
        } else if (queryString.contains("where") == false) {
            String reg = "(.+)(\\sgroup.+)";
            Pattern pattern = Pattern.compile(reg);
            Matcher matcher = pattern.matcher(queryString);
            String ans = "";
            if (matcher.find()) {
                ans = matcher.group(1);
            }
            return ans;
        } else {
            int index = 0;
            String[] splitString = queryString.split(" ");
            for (int i = 0; i < splitString.length; i++) {

                if (splitString[i].equals("where")) {
                    index = i;
                    break;

                } else {

                    base.append(splitString[i]).append(" ");

                }
            }

        }

        if (base != null) {
            base.deleteCharAt(base.length() - 1);

            String baseString = base.toString();

            return baseString;
        } else {
            return "";
        }
    }

    /*
     * This method will extract the fields to be selected from the query string. The
     * query string can have multiple fields separated by comma. The extracted
     * fields will be stored in a String array which is to be printed in console as
     * well as to be returned by the method
     *
     * Note: 1. The field name or value in the condition can contain keywords
     * as a substring. For eg: from_city,job_order_no,group_no etc. 2. The field
     * name can contain '*'
     *
     */

    public String[] getFields(String queryString) {

        String[] splitString = queryString.split(" ");
        int index = 0;
        StringBuilder fields = new StringBuilder();
        for (int i = 0; i < splitString.length; i++) {
            if (splitString[i].equals("select")) {
                index = i + 1;
                break;
            }
        }
        String field = "";
        field += splitString[index];
        String[] ans = field.split(",");
        return ans;


    }

    /*
     * This method is used to extract the conditions part from the query string. The
     * conditions part contains starting from where keyword till the next keyword,
     * which is either group by or order by clause. In case of absence of both group
     * by and order by clause, it will contain till the end of the query string.
     * Note:  1. The field name or value in the condition can contain keywords
     * as a substring. For eg: from_city,job_order_no,group_no etc. 2. The query
     * might not contain where clause at all.
     */

    public String getConditionsPartQuery(String queryString) {

        String query = queryString.toLowerCase();
        if (queryString.isEmpty() || queryString == null) {
            return null;
        }
        if (!queryString.contains("where")) {
            return null;
        }

        String ans = "";
        if (!query.contains("group by") && !query.contains("order by")) {

            String reg = "(where\\s)(.+)";

            Pattern pattern = Pattern.compile(reg);
            Matcher matcher = pattern.matcher(query);
            if (matcher.find()) {
                ans = matcher.group(2);
            }

        } else {
            String reg = "(where\\s)(.+)(\\sgroup\\sby|\\sorder\\sby)";
            Pattern pattern = Pattern.compile(reg);
            Matcher matcher = pattern.matcher(query);
            if (matcher.find()) {
                ans = matcher.group(2);
            }
        }


        return ans;

    }



    /*
     * This method will extract condition(s) from the query string. The query can
     * contain one or multiple conditions. In case of multiple conditions, the
     * conditions will be separated by AND/OR keywords. for eg: Input: select
     * city,winner,player_match from ipl.csv where season > 2014 and city
     * ='Bangalore'
     *
     * This method will return a string array ["season > 2014","city ='bangalore'"]
     * and print the array
     *
     * Note: ----- 1. The field name or value in the condition can contain keywords
     * as a substring. For eg: from_city,job_order_no,group_no etc. 2. The query
     * might not contain where clause at all.
     */

    public String[] getConditions(String queryString) {

        if (queryString == null || queryString.isEmpty()) {
            return null;
        }
        if (!queryString.contains("where")) {
            return null;
        }

        String req = getConditionsPartQuery(queryString);
        String[] str;
        if (req.contains("and") && !req.contains("or")) {
            str = req.split("and");
        } else if (req.contains("or") && !req.contains("and")) {
            str = req.split("\\sor\\s");
        } else if (req.contains("and") && req.contains("or")) {
            str = req.split(" and | or ");
        } else {
            str = req.split(",");
        }

        int i = 0;

        return str;

    }

    /*
     * This method will extract logical operators(AND/OR) from the query string. The
     * extracted logical operators will be stored in a String array which will be
     * returned by the method and the same will be printed Note:  1. AND/OR
     * keyword will exist in the query only if where conditions exists and it
     * contains multiple conditions. 2. AND/OR can exist as a substring in the
     * conditions as well. For eg: name='Alexander',color='Red' etc. Please consider
     * these as well when extracting the logical operators.
     *
     */

    public String[] getLogicalOperators(String queryString) {


        String query = queryString.toLowerCase();
        if (queryString == null || queryString.isEmpty()) {
            return null;
        }
        if (!query.contains("and") && !query.contains("or")) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        Pattern pattern = Pattern.compile(" and|or ");
        Matcher matcher = pattern.matcher(queryString);
        while ((matcher.find())) {
            builder.append(matcher.group().trim()).append(" ");
        }
        String[] ans = builder.toString().split(" ");
        return ans;
    }

    /*
     * This method extracts the order by fields from the query string. Note:
     * 1. The query string can contain more than one order by fields. 2. The query
     * string might not contain order by clause at all. 3. The field names,condition
     * values might contain "order" as a substring. For eg:order_number,job_order
     * Consider this while extracting the order by fields
     */

    public String[] getOrderByFields(String queryString) {

        if (queryString.isEmpty() || queryString == null) {
            return null;
        }

        String query = queryString.toLowerCase();

        String reg = "(order\\sby\\s)(\\w+)";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(query);
        String res = "";
        int check = 0;
        if (matcher.find()) {
            res += matcher.group(2);
            check = 1;
        }
        if (check == 0) {
            return null;
        } else {
            String[] ans = res.split(",");
            return ans;
        }
    }

    /*
     * This method extracts the group by fields from the query string. Note:
     * 1. The query string can contain more than one group by fields. 2. The query
     * string might not contain group by clause at all. 3. The field names,condition
     * values might contain "group" as a substring. For eg: newsgroup_name
     *
     * Consider this while extracting the group by fields
     */

    public String[] getGroupByFields(String queryString) {

        if (queryString == null || queryString.isEmpty()) {
            return null;
        }
        String query = queryString.toLowerCase();

        String reg = "(group\\sby\\s)(\\w+)";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(query);
        String ans = "";
        if (matcher.find()) {
            ans = matcher.group(2);
            String[] res = ans.split(",");


            return res;
        } else {

            return null;
        }
    }

    /*
     * This method extracts the aggregate functions from the query string. Note:
     *  1. aggregate functions will start with "sum"/"count"/"min"/"max"/"avg"
     * followed by "(" 2. The field names might
     * contain"sum"/"count"/"min"/"max"/"avg" as a substring. For eg:
     * account_number,consumed_qty,nominee_name
     *
     * Consider this while extracting the aggregate functions
     */

    public String[] getAggregateFunctions(String queryString) {

        if (queryString.isEmpty() || queryString == null) {
            return null;
        }

        String query = queryString.toLowerCase();
        if (!query.contains("sum") && !query.contains("count") && !query.contains("min") && !query.contains("max") && !query.contains("avg")) {

            return null;
        } else {
            String reg2 = "(sum\\(\\w+\\)|count\\(\\w+\\)|max\\(\\w+\\)|min\\(\\w+\\)|avg\\(\\w+\\))";
            Pattern pattern = Pattern.compile(reg2);
            Matcher matcher = pattern.matcher(query);
            StringBuilder sb = new StringBuilder();
            while (matcher.find()) {
                String ans = matcher.group();
                sb.append(ans).append((" "));
            }
            String ans2 = sb.toString();
            String[] finalAns = ans2.split(" ");

            return finalAns;


        }
    }

}