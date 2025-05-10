import {useEffect, useState} from "react";
import {TodoDTO} from "../types/TodoDTO.ts";
import {todosApi} from "../services/todosApi.ts";
import {TodoInputDTO} from "../types/TodoInputDTO.ts";

type stateProps = {
    todos: TodoDTO[];
    loading: boolean;
    error: string | null;
}

export type TodosApi = ReturnType<typeof useTodos>;

export function useTodos() {
    const [state, setState] = useState<stateProps>({
        todos: [],
        loading: false,
        error: null
    });

    const setTodos = (todos: TodoDTO[]) =>
        setState(prev => ({ ...prev, todos }));

    const addTodo = (todo: TodoDTO)=>
        setState(prev => ({ ...prev, todos: [todo, ...prev.todos] }));

    const setLoading = (loading: boolean) =>
        setState(prev => ({ ...prev, loading }));

    const setError = (error: string | null) =>
        setState(prev => ({ ...prev, error }));

    const updateTodo = (todo: TodoDTO) =>
        setState(prev => ({ ...prev, todos: prev.todos.map(t => t.id === todo.id ? todo : t) }));

    const deleteTodo = (id: string) =>
        setState(prev => ({ ...prev, todos: prev.todos.filter(t => t.id !== id) }));

    const save = (todo: TodoInputDTO) => {
        setLoading(true);
        setError(null);
        return todosApi.add(todo)
            .then(addedTodo => addedTodo ? addTodo(addedTodo) : void 0)
            .catch(error => {
                setError(error.message ?? "Ein unbekannter Fehler ist aufgetreten");
                throw error;
            })
            .finally(() => setLoading(false));
    }

    const update = (todo: TodoInputDTO, id: string) => {
        setLoading(true);
        setError(null);
        return todosApi.update(todo, id)
            .then(updatedTodo => updatedTodo ? updateTodo(updatedTodo) : void 0)
            .catch(error => {
                setError(error.message ?? "Ein unbekannter Fehler ist aufgetreten")
                throw error;
            })
            .finally(() => setLoading(false));
    }

    const remove = (id: string) => {
        setLoading(true);
        setError(null);
        return todosApi.delete(id)
            .then(success => success ? deleteTodo(id) : void 0)
            .catch(error => setError(error.message ?? "Ein unbekannter Fehler ist aufgetreten"))
            .finally(() => setLoading(false));
    }

    const undo = () => {
        setLoading(true);
        setError(null);
        return todosApi.undo()
            .then(todos => todos ? setTodos(todos) : void 0)
            .catch(error => setError(error.message ?? "Ein unbekannter Fehler ist aufgetreten"))
            .finally(() => setLoading(false));
    }

    const redo = () => {
        setLoading(true);
        setError(null);
        return todosApi.redo()
            .then(todos => todos ? setTodos(todos) : void 0)
            .catch(error => setError(error.message ?? "Ein unbekannter Fehler ist aufgetreten"))
            .finally(() => setLoading(false));
    }

    useEffect(() => {
        setLoading(true);
        setError(null);
        todosApi.getAll()
            .then(todos => setTodos(todos))
            .catch(error => setError(error.message ?? "Ein unbekannter Fehler ist aufgetreten"))
            .finally(() => setLoading(false));
    }, []);

    return {
        todos: state.todos,
        saveTodo: save,
        updateTodo: update,
        removeTodo: remove,
        undo: undo,
        redo: redo,
        loading: state.loading,
        error: state.error
    };
}