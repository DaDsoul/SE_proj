import React, {Component} from "react";
import {baseUrl} from "../../shared/BaseUrl";
import {Form, FormGroup, Input, Label} from "reactstrap";
import SearchTicketForAgent from "./SearchTicketForAgent";
import {withRouter} from 'react-router-dom';

class CreateTicket extends Component{
    constructor(props){
        super(props);
        this.collectData = this.collectData.bind(this);
    }
    collectData = (body) => {
        body.owner_document_id = this.docId.value;
        body.owner_document_type = this.doctype.value;
        body.owner_firstname = this.firstame.value;
        body.owner_lastname = this.lastname.value;
        body.token = this.props.admin.admin_token;
        console.log(body);
        if(window.confirm("Do you want to buy ticket?")) {
            return fetch(baseUrl + '/agent/buy-ticket', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(body)
            })
                .then(response => response)
                .then(response => {
                    console.log(response.body)
                    if (response.ok) {
                        let mes = "Thanks, your request is submitted. Do you want to print a ticket?";
                        if (window.confirm(mes)) {
                            this.props.history.push('/home/print_ticket/11');
                        }else{
                            window.location.reload();
                        }
                    } else {
                        var error = new Error("Error " + response.status + ': ' + response.statusText);
                        error.response = response;
                        throw error;
                    }
                })
                .catch(error => {
                    alert(error + "\nTry again");
                });
        }
    };
    render() {
        return (
            <div className='mt-2'>
                <Form>
                    <div className='row'>
                        <FormGroup className='col-6'>
                            <Label for='firstname'>
                                Firstname
                            </Label>
                            <Input type="text" id="firstname" name="firstname"
                                   innerRef={(input) => this.firstame = input}/>
                        </FormGroup>
                        <FormGroup className='col-6'>
                            <Label for='lastname'>
                                Lastname
                            </Label>
                            <Input type="text" id="lastname" name="lastname"
                                   innerRef={(input) => this.lastname = input}/>
                        </FormGroup>
                    </div>
                    <div className='row'>
                        <FormGroup className='col-6'>
                            <Label for="doctype">Select document type</Label>
                            <Input type="select" name="select" id="doctype" innerRef={(input) => this.doctype = input}>
                                <option>National Id</option>
                                <option>Passport</option>
                            </Input>
                        </FormGroup>
                        <FormGroup className='col-6'>
                            <Label for='docId'>
                                Enter ID of your document
                            </Label>
                            <Input type="text" id="docId" name="docId"
                                   innerRef={(input) => this.docId = input}/>
                        </FormGroup>
                    </div>
                    {/*<div className='row mr-auto'>*/}
                    {/*    <button className='btn btn-secondary ml-auto mb-2' onClick={this.handleSubmit}>Buy ticket*/}
                    {/*    </button>*/}
                    {/*</div>*/}
                </Form>
                <SearchTicketForAgent collectData={this.collectData}/>
            </div>
        );
    }
}

export default withRouter(CreateTicket);