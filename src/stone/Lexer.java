package stone;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lixiaoxun on 2016/7/26.
 */
public class Lexer {

    public static String regexPat = "\\s*((//.*)|([0-9]+)|(\"(\\\\\"|\\\\\\\\|\\\\n|[^\"])*\")"
            + "|[A-Z_a-z][A-Z_a-z0-9]*|==|<=|>=|&&|\\|\\||\\p{Punct})?";
    private Pattern pattern = Pattern.compile(regexPat);
    private ArrayList<Token> queue = new ArrayList<Token>();
    private boolean hasMore;
    private LineNumberReader reader;

    public Lexer(Reader r) {
        hasMore = true;
        reader = new LineNumberReader(r);
    }

    public boolean fillQueue(int i) throws ParseException {
        while (i >= queue.size()) {
            if (hasMore)
                readLine();
            else
                return false;
        }
        return true;
    }

    public void readLine() throws ParseException {
        String line;
        try {
            line = reader.readLine();
        }catch (IOException e){
            throw new ParseException(e);
        }
    }


    protected void addToken(int lineNo, Matcher matcher) {
       String m = matcher.group(1);
        if (m != null)
            if (matcher.group(2) == null) {
                Token token;
                if (matcher.group(3) != null)
                    token = new NumToken(lineNo, Integer.parseInt(m));
                else if (matcher.group(4) != null)
                    token = new StrToken(lineNo, toStringLiteral(m));
                else
                    token = new IdToken(lineNo, m);
            }
    }

    protected String toStringLiteral(String s) {
        StringBuilder sb = new StringBuilder();
        int len = s.length() - 1;
        for (int i = 1; i< len; i++) {
            char c = s.charAt(i);
            if (c == '\\' && i + 1 < len) {
                char c2 = s.charAt(i + 1);
                if ( c2 == '"' || c2 == '\\' )
                    c = s.charAt(++i);
                else if (c2 == 'n') {
                    ++i;
                    c = '\n';
                }
            }
            sb.append(c);
        }
        return sb.toString();
    }


    protected static class NumToken extends Token {
        private int value;

        protected NumToken(int line, int val) {
            super(line);
            value = val;
        }

        @Override
        public boolean isNumber() { return true; }
        @Override
        public String getText() { return Integer.toString(value); }
        @Override
        public int getNumber() { return value; }
    }

    protected static class StrToken extends Token {
        private String text;

        protected StrToken(int line, String t) {
            super(line);
            text = t;
        }

        @Override
        public boolean isString() { return true; }
        @Override
        public String getText() { return text; }

    }

    protected static class IdToken extends Token {
        private String text;
        protected IdToken(int line, String id){
            super(line);
            text = id;
        }

        @Override
        public boolean isIdentifier(){ return true; }
        @Override
        public String getText() { return text; }
    }

}