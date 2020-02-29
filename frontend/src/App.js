import React from 'react';
import UserStore from './store/UserStore';
import { observer } from "mobx-react";
import Routes from "./Routes";

import './App.css';


class App extends React.Component{

    async componentDidMount() {
        UserStore.loading = false;
    }

    render() {
        if (UserStore.loading) {
            return (
                <div className="app">
                    <div className="container">
                        Loading, please wait...
                    </div>
                </div>
            );
        }
        else {

            return(
                <div className="app">
                        <Routes />
                </div>
            );
        }
    }
}

export default observer(App);