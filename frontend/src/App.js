import React from 'react';
import { observer } from "mobx-react";
import Routes from "./Routes";

import './App.css';
import 'bootstrap/dist/css/bootstrap.css';


class App extends React.Component{

    render() {
        return(
            <div className="app">
                    <Routes />
            </div>
        );
    }
}

export default observer(App);