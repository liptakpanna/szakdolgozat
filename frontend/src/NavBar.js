import React from 'react';
import {logout} from './Common';

import 'bootstrap/dist/js/bootstrap.js';

class NavBar extends React.Component{
    constructor(props) {
        super(props);
        this.getItemClass= this.getItemClass.bind(this);
    }

    getItemClass(name){
        if(this.props.active === name)
            return "nav-item active_nav"
        else
            return "nav-item"
    }

    render() {
            return(
                <nav className="navbar navbar-expand-md fixed-top navbar-light" style={{backgroundColor: "#e3f2fd"}}>
                    <div className="container">
                        <div className="navbar-header">
                            <a className="navbar-brand " href="/home">DNA</a>
                            <button className="navbar-toggler collapsed" type="button" data-toggle="collapse" data-target="#navbarCollapse" aria-controls="navbarCollapse" aria-expanded="false" aria-label="Toggle navigation">
                                <span className="navbar-toggler-icon"></span>
                            </button>
                        </div>
                        <div className="navbar-collapse collapse" id="navbarCollapse">
                            <ul className="nav navbar-nav mr-auto">
                                <li className={this.getItemClass("home")}><a className="nav-link" href="/home">Home</a> </li>
                                <li className={this.getItemClass("alignments")}> <a className=" nav-link" href="/alignments">Alignments</a></li>
                                {localStorage.getItem("role") === "ADMIN" ? <li className={this.getItemClass("users")}><a className=" nav-link" href="/users">Users</a></li> : null}
                                <li className={this.getItemClass("profile")}><a className="nav-link" href="/profile">Profile</a></li>
                            </ul>
                            <ul className="nav navbar-nav navbar-right">
                                <li className="nav-item mr-5"><span className="navbar-text">User: {localStorage.getItem("username")}</span></li>
                                <li className="nav-item"><a className="nav-link nav-logout" href="/login" onClick={ () =>logout()}>Logout</a></li>
                            </ul>
                        </div>
                    </div>
                </nav>
            );
    }
}

export default NavBar;