import React from 'react';
import {logout} from './Common';

import 'bootstrap/dist/js/bootstrap.js';

class NavBar extends React.Component{
    constructor(props) {
        super(props);
        this.getItemClass= this.getItemClass.bind(this);
    }

    getItemClass(name){
        let className= "nav-item"
        if(this.props.active === name)
            className += " active_nav"
        return className
    }

    getLinkClass(){
        let className= "nav-link"
        if(this.props.disabled)
            className += " disabled"
        return className
    }

    render() {
            return(
                <nav className="navbar navbar-expand-md fixed-top navbar-light" style={{backgroundColor: "#e3f2fd"}}>
                    <div className="container">
                        <div className="navbar-header">
                            <a className={"navbar-brand " + this.props.disabled ? "disabled" : "" } href="/home">DNA</a>
                            <button className="navbar-toggler collapsed" type="button" data-toggle="collapse" data-target="#navbarCollapse" aria-controls="navbarCollapse" aria-expanded="false" aria-label="Toggle navigation">
                                <span className="navbar-toggler-icon"></span>
                            </button>
                        </div>
                        <div className="navbar-collapse collapse" id="navbarCollapse">
                            <ul className="nav navbar-nav mr-auto">
                                <li className={this.getItemClass("home")}><a className={this.getLinkClass()} href="/home">Home</a> </li>
                                <li className={this.getItemClass("alignments")}> <a className={this.getLinkClass()} href="/alignments">Alignments</a></li>
                                {localStorage.getItem("role") === "ADMIN" ? <li className={this.getItemClass("users")}><a className={this.getLinkClass()} href="/users">Users</a></li> : null}
                                <li className={this.getItemClass("profile")}><a className={this.getLinkClass()} href="/profile">Profile</a></li>
                            </ul>
                            <ul className="nav navbar-nav navbar-right">
                                <li className="nav-item mr-5"><span className="navbar-text">User: {localStorage.getItem("username")}</span></li>
                                <li className="nav-item"><a className={this.getLinkClass()+ "nav-logout"} href="/login" onClick={ (e) =>  (this.props.disabled ? e.preventDefault() : logout())}>Logout</a></li>
                            </ul>
                        </div>
                    </div>
                </nav>
            );
    }
}

export default NavBar;