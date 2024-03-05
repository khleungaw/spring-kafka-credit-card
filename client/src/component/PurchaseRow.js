function PurchaseRow({ cardNo, transactionID, amount, timestamp, status }) {
    return (
        <tr className="overview-page-purchase-row">
            <td>{cardNo}</td>
            <td>{transactionID}</td>
            <td>{amount}</td>
            <td>{timestamp}</td>
            <td>{status}</td>
        </tr>
    );
}

export default PurchaseRow;