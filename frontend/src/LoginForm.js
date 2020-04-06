import React from 'react';
import InputField from './InputField';
import SubmitButton from './SubmitButton';
import Cookie from "js-cookie";
import ParticlesBg from 'particles-bg';

class LoginForm extends React.Component{

    constructor(props) {
        super(props);
        this.state = {
            username: '',
            password: '',
            buttonDisabled: false,
            show: false,
            errormessage: "Cannot connect to server. Please try again later.",
            adminEmail: []
        }
    }

    componentDidMount(){
        this.getAdminEmail();
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

    async getAdminEmail(){
        try {
            let response = await fetch(process.env.REACT_APP_API_URL + '/forgotpassword', {
                method: 'get',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                }
            })

            let result = await response.json();
            if (result){
                console.log(result);
                if(result.status === 500) {
                    this.setState({show:true});
                    this.setState({errormessage: result.message})
                }
                else {
                    let emails = JSON.stringify(result);
                    this.setState({adminEmail: emails.substring(1,emails.length-1)})
                }
            }
        }
        catch(e) {
            console.log(e)
        }
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
            })

            let result = await response.json();
            if (result){
                console.log(result);
                if(result.status === 403) {
                    this.setState({errormessage: "Wrong username password combination. If you have forgotten your password please contact an admin: " + this.state.adminEmail})
                    this.setState({show:true});
                    this.setState({buttonDisabled: false})
                }
                else if(result.status === 500) {
                    this.setState({show:true});
                    this.setState({errormessage: result.message})
                    this.setState({buttonDisabled: false})
                }
                else {
                    localStorage.setItem("isLoggedIn", true);
                    Cookie.set("jwtToken", result.jwttoken)
                    localStorage.setItem("username", this.state.username);
                    localStorage.setItem("id", result.id);
                    localStorage.setItem("role", result.role);

                    this.props.history.push('/home')
                }
            }
        }
        catch(e) {
            this.setState({show:true});
            this.setState({errormessage: "Cannot connect to server, please try again later."})
            console.log(e)
            this.resetForm();
        }
    }

    render() {
        return(
            <>
            <div className="container p-5" style={{backgroundColor: "#e3f2fd"}} >
                <form onSubmit={(e) => e.preventDefault()}>
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
                        required={true}
                    />
                    <InputField
                        type='password'
                        class="w-50"
                        placeholder='Enter password'
                        value={this.state.password ? this.state.password : ''}
                        onChange= { (value) => this.setInputValue('password', value)}
                        label ='Password'
                        required={true}
                    />
                    <br/>
                    <SubmitButton
                        text='Login'
                        type='btn-primary btn-lg'
                        disabled={this.state.buttonDisabled}
                        onClick={ () => this.doLogin()}
                    />
                </form>
        { this.state.show ? <div className="alert alert-danger mt-3" role="alert">{this.state.errormessage}</div> : null }
            </div>
            <ParticlesBg color="#acd7fa" num={100} type="cobweb" bg={true} />
            </>
        );
    }
}

export default LoginForm;