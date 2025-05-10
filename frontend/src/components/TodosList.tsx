import {TodoDTO} from "../types/TodoDTO.ts";
import TodoCard from "./TodoCard.tsx";
import "./TodosList.scss";

type Props = {
    todos: TodoDTO[];
    updateTodo?: (todo: TodoDTO) => void;
    deleteTodo?: (id: string) => void;
}

const TodosList = ({todos, updateTodo, deleteTodo}: Props) => {
    return (
        <>
            <ul className="todo-list">
                {todos.map(todo => (<TodoCard key={todo.id} todo={todo} updateTodo={updateTodo} deleteTodo={deleteTodo}/>))}
            </ul>
        </>
    )
}

export default TodosList;