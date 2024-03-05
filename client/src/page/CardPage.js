import {useEffect, useState} from "react";
import "./CardPage.css"
import PurchaseRowInAccount from "../component/PurchaseRowInAccount";

function CardPage() {
    const [cardNoInput, setCardNoInput] = useState("");
    const [entered, setEntered] = useState(false);
    const [cardNo, setCardNo] = useState("---");
    const [cardInfo, setCardInfo] = useState({balance: "---", limit: "---"});
    const [isCardValid, setIsCardValid] = useState(false);
    const [purchases, setPurchases] = useState([]);
    const [transactionAmount, setTransactionAmount] = useState(0);

    useEffect(() => {
        if (entered) {
            getCardInfo(cardNo);
        }
    }, [cardNo, entered]);

    useEffect(() => {
        if (isCardValid) {
            getPurchases(cardNo);
        }
    }, [cardNo, isCardValid]);

    const getCardInfo = (cardNo) => {
        const endpoint = `${process.env.REACT_APP_API_ENDPOINT}/card/${cardNo}`;
        fetch(endpoint)
            .then(res => res.json())
            .then(body => {
                setCardInfo(body);
                setIsCardValid(true);
            })
            .catch(() => {
                alert("Card not found");
                setCardInfo({balance: "---", limit: "---"});
                setIsCardValid(false);
            });
    }

    const getPurchases = (cardNo) => {
        const endpoint = `${process.env.REACT_APP_API_ENDPOINT}/card/${cardNo}/purchase`;
        fetch(endpoint)
            .then(res => res.json())
            .then(body => setPurchases(body))
            .catch(() => setPurchases([]));
    }

    const handleCardNoInputChange = (event) => {
        setCardNoInput(event.target.value);
    }

    const handleCardNoInputSubmit = (event) => {
        event.preventDefault();

        if (cardNoInput === "") {
            alert("Please enter a card number");
            return;
        }
        setCardNo(cardNoInput);
        setEntered(true);
    }

    const handleTransactionAmountChange = (event) => {
        setTransactionAmount(event.target.value);
    }

    const handleTransactionSubmit = (event) => {
        event.preventDefault();
        const endpoint = `${process.env.REACT_APP_API_ENDPOINT}/card/${cardNo}/purchase`;
        fetch(endpoint, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(transactionAmount)
        })
    };

    const handleRefresh = () => {
        getCardInfo(cardNo);
        getPurchases(cardNo);
    }

    return (
        <div className="card-page">
            <div className="card-page-header">
                <div className="card-page-form">
                    <form onSubmit={handleCardNoInputSubmit}>
                        <input
                            type="text"
                            value={cardNoInput}
                            onChange={handleCardNoInputChange}
                            placeholder="Card Number"
                        />
                        <button type="submit">
                            Select Card
                        </button>
                    </form>
                </div>
                {
                    isCardValid ? <div className="card-page-card-info">
                        <div className="card-page-card-info-no">
                            {cardNo}
                        </div>
                        <div className="card-page-card-info-balance">
                            Balance: {cardInfo.balance}
                        </div>
                        <div className="card-page-card-info-limit">
                            Limit: {cardInfo.limit}
                        </div>
                    </div> : <div className="card-page-card-info"></div>
                }
                <div className="card-page-form">
                    <form onSubmit={handleTransactionSubmit}>
                        <input
                            type="number"
                            value={transactionAmount}
                            onChange={handleTransactionAmountChange}
                            placeholder="Transaction Amount"
                            disabled={!isCardValid}
                        />
                        <button type="submit" disabled={!isCardValid}>
                            Submit Transaction
                        </button>
                    </form>
                </div>
            </div>
            <div className="card-page-transaction-history">
                <div className="card-page-transaction-history-title">
                    Transaction History
                    <button onClick={handleRefresh}>Refresh</button>
                </div>
                <table className="card-page-transaction-history-body">
                    <thead>
                        <tr>
                            <td>Transaction ID</td>
                            <td>Amount</td>
                            <td>Timestamp</td>
                            <td>Status</td>
                        </tr>
                    </thead>
                    <tbody>
                    {
                        purchases.map((purchase, index) => {
                            return(<PurchaseRowInAccount key={"purchase"+index} transactionID={purchase.id} amount={purchase.amount} timestamp={purchase.timestamp} status={purchase.status} />)
                        })
                    }
                    </tbody>
                </table>
            </div>
        </div>
    );
}

export default CardPage;