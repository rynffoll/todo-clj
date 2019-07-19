FROM clojure:tools-deps-alpine

COPY target/todo-clj.jar /opt
WORKDIR /opt

CMD java -cp /opt/todo-clj.jar clojure.main -m todo_clj.core
