public enum TaskType {
    COMPUTATIONAL(1) {
        @Override
        public String toString() {
            return "Computationl Task";
        }
    },
    IO(2) {
        @Override
        public String toString() {
            return "IO-Bound Task";
        }
    },
    OTHER(3) {
        @Override
        public String toString() {
            return "Unknown Task";
        }
    };

    private int typePriority;

    private TaskType(int priority) {
        if (validatePriority(priority))
            typePriority = priority;
        else
            throw new IllegalArgumentException("Priority is not an integer");
    }

    public void setPriority(int priority) {
        if (validatePriority(priority))
            this.typePriority = priority;
        else
            throw new IllegalArgumentException("Priority is not an integer");
    }

    public int getPriorityValue() {
        return typePriority;
    }

    public TaskType getType() {
        return this;
    }

    private static boolean validatePriority(int priority) {
        if (priority < 1 || priority > 10)
            return false;
        return true;
    }

    public static void main(String[] args) {

        TaskType t1 = TaskType.COMPUTATIONAL;
        TaskType t2 = TaskType.IO;
        TaskType t3 = TaskType.OTHER;
        System.out.println(t1.getPriorityValue());
        System.out.println(t2.getPriorityValue());
        System.out.println(t3.getPriorityValue());
    }
}
