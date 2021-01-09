package ua.rudkovskyi.payments.util;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PathUtil {
    private static final int MAX_PATH_LENGTH = 4;
    private static final int MIN_PATH_LENGTH = 1;

    private PathUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static long numToLong(String strNum) {
        if (strNum == null) {
            return -1L;
        }
        long num = -1L;
        if (strNum.matches("^\\d{1,19}$"))
            try {
                num = Long.parseLong(strNum);
            }
            catch (NumberFormatException e) {
                return -1L;
            }
        if (num < 0) {
            return -1L;
        } else {
            return num;
        }
    }

    public static List<Long> pathToArray(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            pathInfo = "/";
        }
        String[] path = pathInfo.split("/");
        List<Long> pathLong = new ArrayList<>();
        if (path.length < MIN_PATH_LENGTH || path.length > MAX_PATH_LENGTH) {
            return null;
        }
        for (int i = MIN_PATH_LENGTH; i < path.length && i < MAX_PATH_LENGTH; i++) {
            long number = numToLong(path[i]);
            if (number != -1L) {
                pathLong.add(numToLong(path[i]));
            } else {
                return null;
            }
        }
        return pathLong;
    }
}
