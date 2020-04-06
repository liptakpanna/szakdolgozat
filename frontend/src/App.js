import React from 'react';
import { observer } from "mobx-react";
import Routes from "./Routes";

import './App.css';
import 'bootstrap/dist/css/bootstrap.css';
import 'react-widgets/dist/css/react-widgets.css';
import './font-awesome-4.7.0/css/font-awesome.min.css';

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