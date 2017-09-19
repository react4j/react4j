package gwt.react.todo_mvc.client;

public class utils {
    static String pluralize(int count, String word) {
        return count == 1 ? word : word + 's';
    }
}
