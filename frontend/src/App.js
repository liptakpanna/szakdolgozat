import React from 'react';
import UserStore from './store/UserStore';
import LoginForm from './LoginForm';
import { observer } from "mobx-react";
import IgvBrowser from './IgvBrowser';

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
            if (UserStore.isLoggedIn) {
                return (
                    <div className="app">
                        <div className="container">
                            Welcome {UserStore.username}
                            <IgvBrowser />
                        </div>
                    </div>
                );
            }

            return(
                <div className="app">
                    <div className="container">
                        <LoginForm />
                        <IgvBrowser />
                    </div>
                </div>
            );
        }
    }
}

export default observer(App);