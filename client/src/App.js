import './App.css';
import React from "react";
import { Route, Routes } from "react-router";
import OverviewPage from "./page/OverviewPage";
import CardPage from "./page/CardPage";
import {Link} from "react-router-dom";

function App() {
    return (
        <div className="app">
            <header className="app-header">
                <div className="app-header-link"><Link to={"/"}><h3>Overview</h3></Link></div>
                <div className="app-header-link"><Link to={"/account"}><h3>Account</h3></Link></div>
            </header>
            <Routes>
                <Route path="/" element={<OverviewPage/>} />
                <Route path="/account" element={<CardPage/>} />
            </Routes>
        </div>
    );
}

export default App;
