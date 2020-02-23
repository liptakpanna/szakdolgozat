import React from 'react';
import InputField from './InputField';
import SubmitButton from './SubmitButton';
import UserStore from './store/UserStore';

class LoginForm extends React.Component{

    constructor(props) {
        super(props);
        this.state = {
            username: '',
            password: '',
            buttonDisabled: false
        }
    }

    setInputValue(property, value) {
        value = value.trim();
        if (value.length > 12) {
            return;
        }
        this.setState({
            [property]: value
        })
    }

    resetForm() {
        this.setState({
            username: '',
            password: '',
            buttonDisabled: false
        })
    }

    async doLogin() {
        if (!this.state.username) {return;}
        if (!this.state.password) {return;}

        this.setState({buttonDisabled: true})

        try {
            let response = await fetch(process.env.REACT_APP_API_URL + '/authenticate', {
                method: 'post',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    username: this.state.username,
                    password: this.state.password
                })
            });

            let result = await response.json();
            if (result){
                console.log(result);
                UserStore.isLoggedIn = true;
                UserStore.jwtToken = result.jwttoken;
                UserStore.username = this.state.username;
            }
            else {
                alert("Something went wrong...");
            }
        }
        catch(e) {
            console.log(e)
            this.resetForm();
        }
    }

    render() {
        return(
            <div className="loginContainer">
                <div className="loginForm">
                    <h1 className="welcome">
                        Welcome!
                    </h1>
                    <InputField
                        type='text'
                        placeholder='Enter username'
                        value={this.state.username ? this.state.username : ''}
                        onChange= { (value) => this.setInputValue('username', value)}
                        label ='Username'
                    />
                    <InputField
                        type='password'
                        placeholder='Enter password'
                        value={this.state.password ? this.state.password : ''}
                        onChange= { (value) => this.setInputValue('password', value)}
                        label ='Password'
                    />

                    <SubmitButton
                        text='Login'
                        disabled={this.state.buttonDisabled}
                        onClick={ () => this.doLogin()}
                    />
                </div>
            </div>
        );
    }
}

export default LoginForm;