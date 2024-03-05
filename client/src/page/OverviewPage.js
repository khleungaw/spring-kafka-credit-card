import {useEffect, useState} from "react";
import CardRow from "../component/CardRow";
import PurchaseRow from "../component/PurchaseRow";
import "./OverviewPage.css"

function OverviewPage() {
    const [cards, setCards] = useState([]);
    const [purchases, setPurchases] = useState([]);

    useEffect(() => {
        getCards();
        getPurchases();
    }, []);

    const getCards = () => {
        const endpoint = `${process.env.REACT_APP_API_ENDPOINT}/card`;
        fetch(endpoint)
            .then(res => res.json())
            .then(body => setCards(body));
    }

    const getPurchases = () => {
        const endpoint = `${process.env.REACT_APP_API_ENDPOINT}/card/purchase`;
        fetch(endpoint)
            .then(res => res.json())
            .then(body => setPurchases(body));
    }

    const handleAddCard = () => {
        const endpoint = `${process.env.REACT_APP_API_ENDPOINT}/card`;
        fetch(endpoint, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(50000)
        })
            .then(res => res.json())
            .then(() => getCards());
    }

    return(
        <div className="overview-page">
            <div className="overview-page-card">
                <div className="overview-page-card-title">
                    <div className="overview-page-title-container">
                        <span>Credit Cards</span>
                    </div>
                    <button className="overview-page-add-card-btn" onClick={handleAddCard}>Create</button>
                </div>
                <table className="overview-page-card-body">
                    <thead>
                        <tr>
                            <td>Card Number</td>
                            <td>Balance</td>
                            <td>Limit</td>
                        </tr>
                    </thead>
                    <tbody>
                    {
                        cards.map((card, index) => {
                            return(<CardRow key={"card"+index} cardNo={card.cardNo} balance={card.balance} limit={card.limit} />)
                        })
                    }
                    </tbody>
                </table>
            </div>
            <div className="overview-page-purchase">
                <div className="overview-page-purchase-title">
                    <div className="overview-page-title-container">
                        <span>Purchases</span>
                    </div>
                </div>
                <table className="overview-page-purchase-body">
                    <thead>
                    <tr>
                        <td>Card Number</td>
                        <td>Transaction ID</td>
                        <td>Amount</td>
                        <td>Timestamp</td>
                        <td>Status</td>
                    </tr>
                    </thead>
                    <tbody>
                    {
                        purchases.map((purchase, index) => {
                            return(<PurchaseRow key={"purchase"+index} cardNo={purchase.cardNo} transactionID={purchase.id} amount={purchase.amount} timestamp={purchase.timestamp} status={purchase.status} />)
                        })
                    }
                    </tbody>
                </table>
            </div>
        </div>
    );
}

export default OverviewPage;