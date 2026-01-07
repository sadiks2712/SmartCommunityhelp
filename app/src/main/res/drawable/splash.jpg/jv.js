const contracts = [];
const invoices = [];
const pricing = [];
const sales = [];

const contractForm = document.getElementById("contractForm");
const contractTableBody = document.querySelector("#contractTable tbody");
const contractSelect = document.getElementById("contractSelect");

const invoiceForm = document.getElementById("invoiceForm");
const invoiceTableBody = document.querySelector("#invoiceTable tbody");

const pricingForm = document.getElementById("pricingForm");
const pricingTableBody = document.querySelector("#pricingTable tbody");

const salesForm = document.getElementById("salesForm");
const salesTableBody = document.querySelector("#salesTable tbody");

function formatCurrency(num) {
    return num.toFixed(2);
}

contractForm.addEventListener("submit", function(e) {
    e.preventDefault();
    const buyer = contractForm.buyer.value.trim();
    const product = contractForm.product.value.trim();
    const quantity = parseInt(contractForm.quantity.value);
    const price = parseFloat(contractForm.price.value);

    const totalPrice = quantity * price;

    const contract = { id: contracts.length + 1, buyer, product, quantity, price, totalPrice };
    contracts.push(contract);

    updateContractsTable();
    updateContractSelect();

    contractForm.reset();
});

function updateContractsTable() {
    contractTableBody.innerHTML = "";
    contracts.forEach(c => {
        const row = document.createElement("tr");
        row.innerHTML = `
            <td>${c.buyer}</td>
            <td>${c.product}</td>
            <td>${c.quantity}</td>
            <td>$${formatCurrency(c.price)}</td>
            <td>$${formatCurrency(c.totalPrice)}</td>
        `;
        contractTableBody.appendChild(row);
    });
}

function updateContractSelect() {
    contractSelect.innerHTML = "<option value=''>--Select Contract--</option>";
    contracts.forEach(c => {
        const option = document.createElement("option");
        option.value = c.id;
        option.textContent = `${c.buyer} - ${c.product} (${c.quantity} units)`;
        contractSelect.appendChild(option);
    });
}

invoiceForm.addEventListener("submit", function(e) {
    e.preventDefault();
    const contractId = parseInt(invoiceForm.contractSelect.value);
    const invoiceNumber = invoiceForm.invoiceNumber.value.trim();
    if (!contractId) {
        alert("Please select a contract");
        return;
    }
    const contract = contracts.find(c => c.id === contractId);

    const invoice = { invoiceNumber, contract };
    invoices.push(invoice);

    updateInvoicesTable();

    invoiceForm.reset();
});

function updateInvoicesTable() {
    invoiceTableBody.innerHTML = "";
    invoices.forEach(inv => {
        const row = document.createElement("tr");
        row.innerHTML = `
            <td>${inv.invoiceNumber}</td>
            <td>${inv.contract.buyer}</td>
            <td>${inv.contract.product}</td>
            <td>${inv.contract.quantity}</td>
            <td>$${formatCurrency(inv.contract.totalPrice)}</td>
        `;
        invoiceTableBody.appendChild(row);
    });
}

pricingForm.addEventListener("submit", function(e) {
    e.preventDefault();
    const product = pricingForm.productPrice.value.trim();
    const price = parseFloat(pricingForm.priceValue.value);

    const existingIndex = pricing.findIndex(p => p.product.toLowerCase() === product.toLowerCase());
    if(existingIndex > -1){
        pricing[existingIndex].price = price;
    } else {
        pricing.push({ product, price });
    }
    updatePricingTable();

    pricingForm.reset();
});

function updatePricingTable() {
    pricingTableBody.innerHTML = "";
    pricing.forEach(p => {
        const row = document.createElement("tr");
        row.innerHTML = `
            <td>${p.product}</td>
            <td>$${formatCurrency(p.price)}</td>
        `;
        pricingTableBody.appendChild(row);
    });
}

salesForm.addEventListener("submit", function(e) {
    e.preventDefault();
    const customerName = salesForm.customerName.value.trim();
    const product = salesForm.saleProduct.value.trim();
    const quantity = parseInt(salesForm.saleQuantity.value);
    const price = parseFloat(salesForm.salePrice.value);

    const totalPrice = quantity * price;
    sales.push({ customerName, product, quantity, totalPrice });

    updateSalesTable();

    salesForm.reset();
});

function updateSalesTable() {
    salesTableBody.innerHTML = "";
    sales.forEach(sale => {
        const row = document.createElement("tr");
        row.innerHTML = `
            <td>${sale.customerName}</td>
            <td>${sale.product}</td>
            <td>${sale.quantity}</td>
            <td>$${formatCurrency(sale.totalPrice)}</td>
        `;
        salesTableBody.appendChild(row);
    });
}
