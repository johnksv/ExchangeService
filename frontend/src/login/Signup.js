import * as React from 'react';
import {Form, Text, Checkbox} from 'react-form';
import axios from "axios";
import {loginURL} from "../ServiceURLS";
import {Button, Form as FormStyled, FormGroup, Label, Modal, ModalHeader, ModalBody} from 'reactstrap';


const validate = value => ({
    error: !value ? "Input must contain 'Hello World'" : null
});

export class Signup extends React.Component {

    constructor(props) {
        super(props);
        this.setParams = this.props.setParams;
        this.setClientData = this.props.setClientData;
    }


    handleSubmit = (values, e, formapi) => {
        axios.post(loginURL + "signup", values).then(response => {
            this.setParams(values.email, values.password);
            this.setClientData(response.data);
        }).catch(error => {
            formapi.setError("email", "Email already in use");
        });
    };

    render() {
        const textStyle = {className: "form-control"};
        const {open, toggle} = this.props;
        return (
            <Modal isOpen={open}>
                <ModalHeader>
                    Sign up
                </ModalHeader>
                <ModalBody>
                    <Form onSubmit={this.handleSubmit}>
                        {(form) => (
                            <FormStyled onSubmit={form.submitForm}>
                                <FormGroup>
                                    <Label htmlFor="email">Email</Label>
                                    <Text field="email" placeholder="Email" validate={validate}
                                          required {...textStyle}/>
                                    {form.errors && form.errors.email}
                                </FormGroup>
                                <FormGroup>
                                    <Label htmlFor="password">Password</Label>
                                    <Text field="password" type="password" placeholder="Password"
                                          required {...textStyle}/>
                                    {form.errors && form.errors.password}
                                </FormGroup>
                                <FormGroup>
                                    <Label htmlFor="address">Address</Label>
                                    <Text field="address" placeholder="Address" required {...textStyle}/>
                                    {form.errors && form.errors.address}
                                </FormGroup>
                                <FormGroup>
                                    <Checkbox field="agreesToTerms" required/>
                                    <label htmlFor="agreesToTerms">Terms and conditions</label>
                                </FormGroup>
                                <Button color="primary" type="submit">Sign up</Button>
                                <Button color="secondary" className={"ml-1"} onClick={() => toggle()}>Cancel</Button>
                            </FormStyled>
                        )
                        }
                    </Form>
                </ModalBody>
            </Modal>
        )
    }

}