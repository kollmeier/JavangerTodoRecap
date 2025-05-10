import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Layout from "./Layout.tsx";
import TodosPage from "./components/TodosPage.tsx";
import "./App.scss";

const App: React.FC = () => {
    return (
        <Router>
            <>
                <Routes>
                    <Route path="" element={<Layout />} >
                        <Route index element={<TodosPage />} />
                        <Route path="/:filter" element={<TodosPage />} />
                    </Route>
                </Routes>
            </>
        </Router>
    );
};

export default App;