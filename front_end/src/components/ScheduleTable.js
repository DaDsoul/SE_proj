import React, {Component} from 'react';
import {Loading} from './Loading';
import {Modal, ModalBody, ModalHeader} from 'reactstrap';
import { Link } from 'react-router-dom';


class ScheduleRow extends Component {
    render() {
        const id = this.props.id;
        const origin = this.props.origin;
        const destination = this.props.destination;
        const startDate = this.props.startDate;
        const endDate = this.props.endDate;
        const capacity = this.props.capacity;
        const train = this.props.train;

        return (
            <tr>
                <td>
                    <div align='center'>
                        <h5>{train}</h5>
                        <button className='btn btn-secondary' onClick={() => this.callModal()}>Train route</button>
                        <TrainRoute setClick={click => this.callModal = click} routes={this.props.routes}/>
                    </div>
                </td>
                <td>
                    <div align='center'>
                        <h5>{origin}</h5>
                        <h6>Departure Time: {startDate}</h6>
                    </div>
                </td>
                <td>
                    <div align='center'>
                        <h5>{destination}</h5>
                        <h6>Arrival Time: {endDate}</h6>
                    </div>
                </td>

                <td>
                    <div align='center'>
                        <h5 align='center'>{capacity}</h5>
                        {capacity > 0 ?
                            <Link className='btn btn-secondary' to={`/buy_ticket/${id}`}>Buy a ticket</Link>
                            :
                            <button className='btn btn-secondary disabled'>No tickets</button>
                        }
                    </div>
                </td>
            </tr>
        );
    }
}
class ScheduleTable extends Component{

    constructor(props){
        super(props);

        this.handleBuy = this.handleBuy.bind(this);
    }

    handleBuy = (id) =>{
        var message = "Are you sure?\nThis will cost you some money";
        if (window.confirm(message))
            console.log("This " + id + " will be send to agent");
    }

    render() {
        const rows = []
        this.props.schedule.schedule.forEach((route) => {
            rows.push(
                <ScheduleRow key={route.id}
                    id = {route.id}
                    origin = {route.origin}
                    destination = {route.destination}
                    train = {route.train.trainId}
                    capacity = {route.train.capacity}
                    startDate = {route.startTime}
                    endDate = {route.endTime}
                    handleBuy = {this.handleBuy}
                    routes = {route.routes}
                />
            );
        });

        if(!this.props.schedule.isLoading) {
            if (this.props.schedule.schedule.length === 0 && this.props.schedule.req === 0)
                return (
                    <div></div>
                )
            else if(this.props.schedule.schedule.length === 0 && this.props.schedule.req === 1)
                return (
                    <div>NO such routes</div>
                )
            else
                return (
                    <table className='mt-3 table table-striped table-responsive-md btn-table'>
                        <thead>
                        <tr>
                            <th>
                                <div align='center'>Train Number</div>
                            </th>
                            <th>
                                <div align='center'>Origin</div>
                            </th>
                            <th>
                                <div align='center'>Destination</div>
                            </th>
                            <th>
                                <div align='center'>Free Spaces</div>
                            </th>
                        </tr>
                        </thead>
                        <tbody>{rows}</tbody>
                    </table>
                )
        }
        else
            return <Loading/>
    }
}

class TrainRoute extends Component{
    constructor(props){
        super(props);
        this.state = {
            isModalOpen: false
        };
        this.toggleModal = this.toggleModal.bind(this);

    }
    componentDidMount() {
        this.props.setClick(this.toggleModal);
    }

    toggleModal() {
        this.setState({isModalOpen: !this.state.isModalOpen});
    }

    render() {
        return (
            <Modal isOpen={this.state.isModalOpen} toggle={this.toggleModal}>
                <ModalHeader toggle={this.toggleModal} >
                        {this.props.routes[0].origin} -> {this.props.routes[this.props.routes.length - 1].destination}
                </ModalHeader>
                <ModalBody>
                    <table className="table">
                        <thead className="thead-dark">
                        <tr>
                            <th scope="col">#</th>
                            <th scope="col">Station</th>
                            <th scope="col">Arrival Time</th>
                            <th scope="col">Departure Time</th>
                        </tr>
                        </thead>
                        <tbody>
                            <RenderTrainRoutes routes={this.props.routes}/>
                        </tbody>
                    </table>
                </ModalBody>
            </Modal>
        );
    }

}

const RenderTrainRoutes = (props)=>{
    const iter = []
    var len = props.routes.length;
    for(let i = 0; i < len; i++)
    {
        let route = props.routes[i];
        let index = i;
        let newRow =
            <tr key={index}>
                <td>
                    {index + 1}
                </td>
                <td>
                    {route.origin}
                </td>
                <td>
                    {i === 0 ? "" : props.routes[i-1].endDate}
                </td>
                <td>
                    {route.startDate}
                </td>
            </tr>
        iter.push(newRow);
    }
    iter.push(
        <tr key={len}>
            <td>
                {len + 1}
            </td>
            <td>
                {props.routes[len-1].destination}
            </td>
            <td>
                {props.routes[len-1].endDate}
            </td>
            <td>
            </td>
        </tr>);
    return iter;

};

export default ScheduleTable;