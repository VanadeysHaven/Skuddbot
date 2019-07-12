package me.Cooltimmetje.Skuddbot.Utilities.TableUtilities;

/**
 * This is a class to draw neatly looking tables easily.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.62-ALPHA
 * @since v0.4.62-ALPHA
 */
public class TableDrawer {

    private static final boolean DEFAULT_DRAW_HEADER = true;
    private static final int DEFAULT_PADDING = 1;

    private String[][] data;
    private int[] longestString;
    private boolean drawHeader;
    private int padding;
    private int colums;

    public TableDrawer(String[][] data, boolean drawHeader, int padding) throws IllegalArgumentException{
        int length = data[0].length;
        for(int i=1; i < data.length; i++){
            if(data[i].length != length)
                throw new IllegalArgumentException("The nested arrays must be of the same length");
        }

        this.data = data;
        cleanData();
        this.drawHeader = drawHeader;
        this.colums = this.data[0].length;
        this.longestString = findLongestStrings();
        this.padding = padding;
    }

    public TableDrawer(String[][] data, boolean drawHeader){
        this(data, drawHeader,DEFAULT_PADDING);
    }

    public TableDrawer(String[][] data, int padding){
        this(data, DEFAULT_DRAW_HEADER, padding);
    }

    public TableDrawer(String[][] data){
        this(data, DEFAULT_DRAW_HEADER, DEFAULT_PADDING);
    }

    public String drawTable(){
        StringBuilder sb = new StringBuilder();
        sb.append(drawHorizontalDivider(TableDividers.DOWN_RIGHT, TableDividers.HORIZONTAL_DOWN, TableDividers.DOWN_LEFT));
        for(int i=0; i<data.length; i++){
            sb.append(drawDataLine(data[i], TableDividers.VERTICAL));
            if(i == 0 && drawHeader)
                sb.append(drawHorizontalDivider(TableDividers.VERTICAL_RIGHT, TableDividers.CENTRAL, TableDividers.VERTICAL_LEFT));
        }
        sb.append(drawHorizontalDivider(TableDividers.UP_RIGHT, TableDividers.HORIZONTAL_UP, TableDividers.UP_LEFT));
        return sb.toString().trim();
    }

    private int[] findLongestStrings(){
        int[] longestStrings = new int[colums];
        for(int i=0; i < colums; i++){
            int longestString = 0;
            for(int j=0; j < data.length; j++){
                int length = data[j][i].length();
                if(length > longestString)
                    longestString = length;
            }
            longestStrings[i] = longestString;
        }
        return longestStrings;
    }

    private String drawHorizontalDivider(TableDividers prefix, TableDividers glue, TableDividers suffix) {
        StringBuilder sb = new StringBuilder();
        sb.append(prefix.getCharacter());

        for (int i=0; i < longestString.length; i++) {
            sb.append(repeat(TableDividers.HORIZONTAL, longestString[i] + (padding * 2)));
            if (i != longestString.length - 1)
                sb.append(glue.getCharacter());
        }

        sb.append(suffix.getCharacter()).append("\n");
        return sb.toString();
    }

    private String drawDataLine(String[] data, TableDividers glue){
        StringBuilder sb = new StringBuilder();
        sb.append(glue.getCharacter());

        for(int i=0; i < data.length; i++) {
            sb.append(repeat(" ", padding));
            sb.append(data[i]);
            sb.append(repeat(" ", longestString[i] - data[i].length() + padding));
            sb.append(glue.getCharacter());
        }

        return sb.append("\n").toString();
    }

    private String repeat(TableDividers tableDivider, int amount){
        return repeat(tableDivider.getCharacter(), amount);
    }

    private String repeat(String string, int amount){
        StringBuilder sb = new StringBuilder();

        for(int i=0; i<amount; i++)
            sb.append(string);

        return sb.toString();
    }

    private void cleanData() {
        for(int i=0; i < data.length; i++)
            for(int j=0; j < data[i].length; j++)
                if(data[i][j] == null)
                    data[i][j] = "null";
    }


}
