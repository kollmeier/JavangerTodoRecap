import {TodoDTO} from "../types/TodoDTO.ts";
import "./TodoCard.scss"
import todoStatusDisplayConverter from "../services/todoStatusDisplayConverter.ts";

type Props = {
    todo: TodoDTO
    updateTodo?: (todo: TodoDTO) => void
    deleteTodo?: (id: string) => void
}

const TodoCard = ({todo, updateTodo, deleteTodo}: Props) => {

    return (
       <li className={"todo-card todo-card__" + todo.status.toLowerCase()}>
           <div>
               <span className="todo-description">{todo.description}</span>
               <span className="todo-date">{todo.createdAt}</span>
               <span className="todo-status">{todoStatusDisplayConverter.shortDisplayString(todo.status)}</span>
               <span className="todo-actions">
                   {updateTodo && (<button type="button" onClick={() => updateTodo(todo)}>Hochstufen</button>) }
                   {deleteTodo && (<button type="button" className="button--delete" onClick={() => deleteTodo(todo.id)}>Löschen</button>) }
               </span>
           </div>
       </li>
    );
}

export default TodoCard;