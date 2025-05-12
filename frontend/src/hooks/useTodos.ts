import {useEffect, useState} from "react";
import {TodoDTO} from "../types/TodoDTO.ts";
import {todosApi} from "../services/todosApi.ts";
import {TodoInputDTO} from "../types/TodoInputDTO.ts";
import {AxiosError} from "axios";
import {isErrorDTO} from "../types/ErrorDTO.ts";

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
        setState(prev => ({...prev, todos}));

    const addTodo = (todo: TodoDTO) =>
        setState(prev => ({...prev, todos: [todo, ...prev.todos]}));

    const setLoading = (loading: boolean) =>
        setState(prev => ({...prev, loading}));

    const setError = (error: string | null) =>
        setState(prev => ({...prev, error}));

    const setErrorFromCaughtError = (error: unknown)=> {
        if (error instanceof AxiosError) {
            const response = error.response;
            if (response && response.data && isErrorDTO(response.data)) {
                setError(response.data.message);
                return;
            }
        }
        if (error instanceof Error) {
            setError(error.message);
        }
        setError("Ein unbekannter Fehler ist aufgetreten");
    }

    const updateTodo = (todo: TodoDTO) =>
        setState(prev => ({ ...prev, todos: prev.todos.map(t => t.id === todo.id ? todo : t) }));

    const deleteTodo = (id: string) =>
        setState(prev => ({ ...prev, todos: prev.todos.filter(t => t.id !== id) }));

    const save = (todo: TodoInputDTO, force?: boolean) => {
        setLoading(true);
        setError(null);
        return todosApi.add(todo, force)
            .then(addedTodo => addedTodo ? addTodo(addedTodo) : void 0)
            .catch(error => {
                setErrorFromCaughtError(error);
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
                setErrorFromCaughtError(error);
                throw error;
            })
            .finally(() => setLoading(false));
    }

    const remove = (id: string) => {
        setLoading(true);
        setError(null);
        return todosApi.delete(id)
            .then(success => success ? deleteTodo(id) : void 0)
            .catch(error => {
                setErrorFromCaughtError(error);
                throw error;
            })
            .finally(() => setLoading(false));
    }

    const undo = () => {
        setLoading(true);
        setError(null);
        return todosApi.undo()
            .then(todos => todos ? setTodos(todos) : void 0)
            .catch(error => {
                setErrorFromCaughtError(error);
                throw error;
            })
            .finally(() => setLoading(false));
    }

    const redo = () => {
        setLoading(true);
        setError(null);
        return todosApi.redo()
            .then(todos => todos ? setTodos(todos) : void 0)
            .catch(error => {
                setErrorFromCaughtError(error);
                throw error;
            })
            .finally(() => setLoading(false));
    }

    useEffect(() => {
        setLoading(true);
        setError(null);
        todosApi.getAll()
            .then(todos => setTodos(todos))
            .catch(error => {
                setErrorFromCaughtError(error);
                throw error;
            })
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
        error: state.error,
        clearError: () => setError(null)
    };
}