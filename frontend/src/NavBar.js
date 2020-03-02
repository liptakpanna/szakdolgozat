import React from 'react';

class NavBar extends React.Component{
    render() {
        if(localStorage.getItem("role") === "ADMIN"){
            return(
                <div className="sidenav">
                    <a className="active" href="/home">Home</a>
                    <a href="/alignments">Alignments</a>
                    <a href="/users">Users</a>
                    <a href="/profile">Profile</a>
                    <a href="/about">About</a>
                    <a href="/igv">IGV TEST</a>
                    <a href="/login" onClick={ () => this.logout()}>Logout</a>
                </div>
            );
        } else {
            return(
                <div className="sidenav">
                    <a className="active" href="/home">Home</a>
                    <a href="/alignments">Alignments</a>
                    <a href="/profile">Profile</a>
                    <a href="/about">About</a>
                    <a href="/igv">IGV TEST</a>
                    <a href="/login" onClick={ () => this.logout()}>Logout</a>
                </div>
            );
        }
        
    }

    logout() {
        localStorage.setItem("isLoggedIn", false);
        localStorage.setItem("jwtToken", "");
        localStorage.setItem("username", "");
        localStorage.setItem("id", "");
        localStorage.setItem("role", "");
        console.log("Logged out");
    }
}

export default NavBar;