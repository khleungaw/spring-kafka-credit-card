function CardRow({ cardNo, balance, limit }) {
    return (
        <tr className="overview-page-card-row">
            <td>{cardNo}</td>
            <td>{balance}</td>
            <td>{limit}</td>
        </tr>
    );
}

export default CardRow;