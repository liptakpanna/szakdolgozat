import React from 'react';
import InputField from './InputField';
import SubmitButton from './SubmitButton';

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
                localStorage.setItem("isLoggedIn", true);
                localStorage.setItem("jwtToken", result.jwttoken);
                localStorage.setItem("username", this.state.username);
                localStorage.setItem("id", result.id);
                localStorage.setItem("role", result.role);

                this.props.history.push('/home')
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
            <div className="container p-5" style={{backgroundColor: "#e3f2fd"}} >
                <form>
                    <h1>
                        Welcome!
                    </h1>
                    <InputField
                        type='text'
                        class="w-50"
                        placeholder='Enter username'
                        value={this.state.username ? this.state.username : ''}
                        onChange= { (value) => this.setInputValue('username', value)}
                        label ='Username'
                    />
                    <InputField
                        type='password'
                        class="w-50"
                        placeholder='Enter password'
                        value={this.state.password ? this.state.password : ''}
                        onChange= { (value) => this.setInputValue('password', value)}
                        label ='Password'
                    />
                    <br/>
                    <SubmitButton
                        text='Login'
                        type='btn-primary btn-lg'
                        disabled={this.state.buttonDisabled}
                        onClick={ () => this.doLogin()}
                    />
                </form>
            </div>
        );
    }
}

export default LoginForm;