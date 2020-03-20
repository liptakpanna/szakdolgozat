import React from 'react';
import {logout} from './Common';

import 'bootstrap/dist/js/bootstrap.js';

class NavBar extends React.Component{

    render() {
        if(localStorage.getItem("role") === "ADMIN"){
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
                                <li className="nav-item"><a className="nav-link" href="/home">Home</a> </li>
                                <li className="nav-item"> <a className=" nav-link" href="/alignments">Alignments</a></li>
                                <li className="nav-item"><a className=" nav-link" href="/users">Users</a></li>
                                <li className="nav-item"><a className="nav-link" href="/profile">Profile</a></li>
                            </ul>
                            <ul className="nav navbar-nav navbar-right">
                                <li className="nav-item mr-5"><span className="navbar-text">User: {localStorage.getItem("username")}</span></li>
                                <li className="nav-item"><a className="nav-link nav-logout" href="/login" onClick={ () =>logout()}>Logout</a></li>
                            </ul>
                        </div>
                    </div>
                </nav>
            );
        } else {
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
                                <li className="nav-item active"><a className="nav-link" href="/home">Home</a></li>
                                <li className="nav-item"> <a className=" nav-link" href="/alignments">Alignments</a></li>
                                <li className="nav-item"><a className="nav-link" href="/profile">Profile</a></li>
                            </ul>
                            <ul className="nav navbar-nav navbar-right">
                                <li className="nav-item"><a className="nav-link" href="/login" onClick={ () =>logout()}>Logout</a></li>
                            </ul>
                        </div>
                    </div>
                </nav>
            );
        }
        
    }
}

export default NavBar;