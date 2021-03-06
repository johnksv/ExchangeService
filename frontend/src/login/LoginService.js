import * as React from 'react';
import {TokenContext} from "../Contexts";
import {Row, Col, Button} from 'reactstrap';
import {Client} from "./Client";

export class LoginService extends React.Component {

    render() {
        return (
            <TokenContext.Consumer>
                {value => this.renderBasedOnValue(value)}
            </TokenContext.Consumer>
        );

    }

    renderBasedOnValue = (context) => {
        const {auth, client, resetAuthParams, toggleSignup, toggleLogin, updatePassword, updateAddress} = context;
        if (!auth) {
            return (<React.Fragment>
                    <Row>
                        <Col md={12}>
                            <h1>You are not logged in yet</h1>
                            Please login or sign up.
                            <br/>
                            <Button className={"mr-2"} color={"primary"}
                                    onClick={() => toggleLogin()}>Login</Button>
                            <Button color={"primary"} onClick={() => toggleSignup()}>Sign up</Button>
                        </Col>
                    </Row>
                </React.Fragment>
            )
        }
        return (
            <Row>
                <Col md={12}>
                    <Client data={client} auth={auth} onUpdatePassword={updatePassword} onUpdateAddress={updateAddress}/>
                    <Button onClick={() => resetAuthParams()} color={"primary"} className={"mt-2 mb-4"}>Log out</Button>

                </Col>
            </Row>
        )
    }


}