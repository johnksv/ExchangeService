import * as React from 'react';
import {NavigationMenu} from './home/NavigationMenu';
import {TokenContext} from "./Contexts";
import {Signup} from "./login/Signup";
import {Login} from "./login/Login";


export class Layout extends React.Component {

    constructor(props) {
        super(props);


        this.state = {
            auth: undefined,
            setAuthParams: this.setAuthParams.bind(this),
            resetAuthParams: this.resetAuthParams.bind(this),
            client: undefined,
            setClientData: this.setClientData.bind(this),
            updatePassword: this.updatePassword.bind(this),
            updateAddress: this.updateAddress.bind(this),
            loginOpen: false,
            signupOpen: false,
            toggleLogin: this.toggleLogin.bind(this),
            toggleSignup: this.toggleSignup.bind(this)
        };

    }

    setAuthParams(username, password) {
        this.setState({
            auth: {
                username,
                password
            },
            loginOpen: false,
            signupOpen: false,
        });
    };

    updatePassword(password) {
        const {auth}= this.state;
        const newAuth = Object.assign({}, auth);
        newAuth.password = password;
        this.setState({
            auth: newAuth
        });
    }

    updateAddress(address) {
        const {client}= this.state;
        const newClient =  Object.assign({}, client);
        newClient.address = address;
        this.setState({
            client: newClient
        });
    }

    resetAuthParams() {
        this.setState({
            auth: undefined,
            loginOpen: false,
            signupOpen: false,
        });
    };

    setClientData(clientdata) {
        this.setState({
                client: clientdata
            }
        );
    };

    toggleLogin() {
        this.setState({
            loginOpen: !this.state.loginOpen
        });
    }

    toggleSignup() {
        this.setState({
            signupOpen: !this.state.signupOpen
        });
    }

    render() {
        const {auth, setAuthParams, setClientData, signupOpen, loginOpen, toggleSignup, toggleLogin, resetAuthParams} = this.state;
        return <TokenContext.Provider value={this.state}>
            <NavigationMenu isAuth={auth !== undefined}
                            signup={toggleSignup}
                            login={toggleLogin}
                            restAuth={resetAuthParams}/>

            <div className='container-fluid'>
                {this.props.children}
                <Login setParams={setAuthParams} setClientData={setClientData} open={loginOpen} toggle={toggleLogin}/>
                <Signup setParams={setAuthParams} setClientData={setClientData} open={signupOpen}
                        toggle={toggleSignup}/>
            </div>
        </TokenContext.Provider>;
    }
}