class Task {
	int starting_point;
	int end_point;
	String alphabet, taskname, time;
	/* A constructor which prevents instance of class value
		from being badly modified.*/
	Task(String alphabet, String taskname, int starting_point, int end_point,
		String time) {
		this.alphabet = alphabet;
		this.taskname = taskname;
		this.starting_point = starting_point;
		this.end_point = end_point;
		this.time = time;
	}
	//--- Getter ---//
	String get_alphabet() {
		return alphabet;
	}
	String get_taskname() {
		return taskname;
	}
	String get_time() {
		return time;
	}
	int get_starting_point() {
		return starting_point;
	}
	int get_end_point() {
		return end_point;
	}
} 