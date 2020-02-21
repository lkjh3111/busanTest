//package com.template
//
//import com.r3.corda.lib.tokens.contracts.types.TokenType
//import com.template.MainController.DividendReceivable
//import com.template.MainController.StockToken
//import demo.DividendReceivableState
//import demo.Stock
//import javafx.beans.value.ChangeListener
//import javafx.beans.value.ObservableValue
//import javafx.collections.FXCollections
//import javafx.collections.ObservableList
//import javafx.event.ActionEvent
//import javafx.geometry.Pos
//import javafx.scene.Scene
//import javafx.scene.control.*
//import javafx.scene.control.cell.PropertyValueFactory
//import javafx.scene.layout.VBox
//import javafx.scene.text.Text
//import javafx.stage.Modality
//import javafx.stage.Stage
//
//import javafx.util.Callback
//import net.corda.core.contracts.Amount
//import net.corda.core.identity.Party
//import net.corda.core.messaging.CordaRPCOps
//import net.corda.core.messaging.FlowHandle
//import net.corda.core.node.NodeInfo
//import net.corda.core.node.services.Vault
//import net.corda.core.node.services.vault.QueryCriteria
//import net.corda.core.transactions.SignedTransaction
//import sun.tools.jconsole.Plotter
//
//import java.math.BigDecimal
//import java.text.DecimalFormat
//import java.text.SimpleDateFormat
//import java.util.Date
//
//class MainController {
//    var helloWorld: Label? = null
//    var IM_currencyComboBox: ComboBox? = null
//    var IM_amount: TextField? = null
//    var IM_recipient: ComboBox? = null
//    var PORT_hkd: Label? = null
//    var T_fiat_issuance: Tab? = null
//    var TP: TabPane? = null
//    var PORT_gbp: Label? = null
//    var PORT_sgd: Label? = null
//    var PORT_usd: Label? = null
//    var T_stock_issuance: Tab? = null
//    var IS_symbol: TextField? = null
//    var IS_name: TextField? = null
//    var IS_currency: ComboBox? = null
//    var IS_issueVol: TextField? = null
//
//    private val stockObservableList = FXCollections.observableArrayList()
//    private val stockTokenObservableList = FXCollections.observableArrayList()
//    private val drObservableList = FXCollections.observableArrayList()
//
//    private val txnObservableList = FXCollections.observableArrayList()
//
//
//    var IS_stockTable: TableView<Stock>? = null
//    var IS_dividend: TextField? = null
//    var IS_exDate: TextField? = null
//    var IS_payDate: TextField? = null
//    var MF_currencyComboBox: ComboBox? = null
//    var MF_amount: TextField? = null
//    var MF_recipient: ComboBox? = null
//    var MS_recipient: ComboBox? = null
//    var MS_amount: TextField? = null
//    var MS_symbol: TextField? = null
//    var PORT_stockTable: TableView? = null
//    var PORT_dividendReceivableTable: TableView? = null
//    var TXN_table: TableView? = null
//
//
//    private var primaryStage: Stage? = null
//
//    private var proxy: CordaRPCOps? = null
//
//
//    fun init(_primaryStage: Stage, _proxy: CordaRPCOps, role: String) {
//
//
//        if (role.equals("E") === false) {
//            // T_fiat_issuance.getGraphic().setVisible(false);
//            TP!!.getTabs().remove(T_fiat_issuance)
//        }
//
//
//        if (role.equals("I") === false) {
//            // T_fiat_issuance.getGraphic().setVisible(false);
//            TP!!.getTabs().remove(T_stock_issuance)
//        }
//
//
//
//        proxy = _proxy
//        System.out.println("proxy is set")
//
//
//        primaryStage = _primaryStage
//
//        System.out.println("init")
//        IM_currencyComboBox!!.getItems().addAll("GBP", "HKD", "SGD", "USD")
//        IS_currency!!.getItems().addAll("GBP", "HKD", "SGD", "USD")
//        MF_currencyComboBox!!.getItems().addAll("GBP", "HKD", "SGD", "USD")
//        System.out.println("init 1")
//
//
//        IM_amount!!.textProperty().addListener(object : ChangeListener<String>() {
//            @Override
//            fun changed(observable: ObservableValue<out String>, oldValue: String, newValue: String) {
//                if (!newValue.matches("\\d{0,7}([\\.]\\d{0,4})?")) {
//                    IM_amount!!.setText(oldValue)
//                }
//            }
//        })
//
//        System.out.println("init 5")
//
//        val iter = proxy!!.networkMapSnapshot().iterator()
//        while (iter.hasNext()) {
//            //System.out.println(c.getCurrencyCode().toString());
//            val ni = iter.next()
//            IM_recipient!!.getItems().add(ni.legalIdentities[0].name.organisation)
//            MF_recipient!!.getItems().add(ni.legalIdentities[0].name.organisation)
//            MS_recipient!!.getItems().add(ni.legalIdentities[0].name.organisation)
//
//        }
//
//        System.out.println("init 6")
//
//        //init all tables
//        setTableappearance()
//
//
//        IS_stockTable!!.setItems(stockObservableList)
//        PORT_stockTable!!.setItems(stockTokenObservableList)
//        PORT_dividendReceivableTable!!.setItems(drObservableList)
//        TXN_table!!.setItems(txnObservableList)
//
//
//        //for stock table
//        val colSymbol = TableColumn("Symbol")
//        colSymbol.setCellValueFactory(PropertyValueFactory("symbol"))
//
//        val colName = TableColumn("Name")
//        colName.setCellValueFactory(PropertyValueFactory("name"))
//
//        val colCurrency = TableColumn("Currency")
//        colCurrency.setCellValueFactory(PropertyValueFactory("currency"))
//
//
//        val colDividend = TableColumn("Dividend")
//        colDividend.setCellValueFactory(PropertyValueFactory("dividend"))
//        colDividend.setEditable(true)
//
//
//        IS_stockTable!!.getColumns().addAll(colSymbol, colName, colCurrency, colDividend)
//
//        //for stock Token table
//        val portStockColSymbol = TableColumn("Stock Symbol")
//        portStockColSymbol.setCellValueFactory(PropertyValueFactory("symbol"))
//
//        val portStockColName = TableColumn("Name")
//        portStockColName.setCellValueFactory(PropertyValueFactory("name"))
//
//        val portStockColCurrency = TableColumn("Currency")
//        portStockColCurrency.setCellValueFactory(PropertyValueFactory("currency"))
//
//        val portStockColAmount = TableColumn("# of Token")
//        portStockColAmount.setCellValueFactory(PropertyValueFactory("amount"))
//
//
//        val portStockColDividend = TableColumn("Upcoming Dividend")
//        portStockColDividend.setCellValueFactory(PropertyValueFactory("dividend"))
//
//
//        val portStockColExDate = TableColumn("Ex-Date")
//        portStockColExDate.setCellValueFactory(PropertyValueFactory("exDate"))
//
//
//        val portStockColPayDate = TableColumn("Pay-Date")
//        portStockColPayDate.setCellValueFactory(PropertyValueFactory("payDate"))
//
//
//        PORT_stockTable!!.getColumns().addAll(portStockColSymbol, portStockColAmount, portStockColDividend, portStockColExDate, portStockColPayDate)
//
//        //for dividend receivable table
//        /* private String issuer;
//        private String payDate;
//        private BigDecimal dividendAmount;
//        private boolean isPay;*/
//        val portDrColIssuer = TableColumn("Issuer")
//        portDrColIssuer.setCellValueFactory(PropertyValueFactory("issuer"))
//
//
//        val portDrColPayDate = TableColumn("Pay Date")
//        portDrColPayDate.setCellValueFactory(PropertyValueFactory("payDate"))
//
//        val portDrColDividendAmoubnt = TableColumn("Dividend Receivable")
//        portDrColDividendAmoubnt.setCellValueFactory(PropertyValueFactory("dividendAmount"))
//
//
//        val portDrColIsPay = TableColumn("Paid?")
//        portDrColIsPay.setCellValueFactory(PropertyValueFactory("isPay"))
//
//
//        PORT_dividendReceivableTable!!.getColumns().addAll(portDrColDividendAmoubnt, portDrColIssuer, portDrColPayDate, portDrColIsPay)
//
//
//        //for transaction
//        val txnColID = TableColumn("Transaction ID")
//        txnColID.setCellValueFactory(PropertyValueFactory("id"))
//
//
//        val txnColInputs = TableColumn("InputRefs.toString()")
//        txnColInputs.setCellValueFactory(PropertyValueFactory("inputs"))
//
//        val txnColOutputs = TableColumn("Outputs.toString()")
//        txnColOutputs.setCellValueFactory(PropertyValueFactory("outputs"))
//
//        val txnColCommands = TableColumn("Commands.toString()")
//        txnColCommands.setCellValueFactory(PropertyValueFactory("command"))
//
//        TXN_table!!.getColumns().addAll(txnColID, txnColInputs, txnColOutputs, txnColCommands)
//
//
//
//        addButtonToStockTable()
//        addButtonToStockTokenTable()
//        addButtonToDRTable()
//
//
//        retrieveStock()
//        retrievePortfolio()
//
//
//        retrieveTransaction()
//
//
//    }
//
//    private fun setTableappearance() {
//        IS_stockTable!!.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY)
//        //   IS_stockTable.setPrefWidth(500);
//        // IS_stockTable.setPrefHeight(0);
//
//
//        PORT_stockTable!!.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY)
//        //    PORT_stockTable.setPrefWidth(500);
//        //  PORT_stockTable.setPrefHeight(300);
//
//        PORT_dividendReceivableTable!!.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY)
//        //  PORT_dividendReceivableTable.setPrefWidth(500);
//        //  PORT_dividendReceivableTable.setPrefHeight(300);
//
//        TXN_table!!.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY)
//
//    }
//
//    /*
//    private void fillTableObservableListWithSampleStock() {
//
//
//
//        stockObservableList.addAll(new Stock(1, "app1"),
//                new Stock(2, "app2"),
//                new Stock(3, "app3"),
//                new Stock(4, "app4"),
//                new Stock(5, "app5"));
//    }*/
//
//    private fun addButtonToStockTable() {
//        val colBtn = TableColumn("..")
//
//        val cellFactory = object : Callback<TableColumn<Stock, Void>, TableCell<Stock, Void>>() {
//            @Override
//            fun call(param: TableColumn<Stock, Void>): TableCell<Stock, Void> {
//                return object : TableCell<Stock, Void>() {
//
//                    private val btn = Button("Edit")
//
//                    init {
//                        btn.setOnAction({ event: ActionEvent ->
//                            val stock = getTableView().getItems().get(getIndex())
//                            System.out.println("selectedStock: $stock")
//                            IS_symbol!!.textProperty().setValue(stock.symbol)
//                            IS_name!!.textProperty().setValue(stock.name)
//                            IS_currency!!.setValue(stock.currency)
//
//                        })
//                    }
//
//                    @Override
//                    fun updateItem(item: Void, empty: Boolean) {
//                        super.updateItem(item, empty)
//                        if (empty) {
//                            setGraphic(null)
//                        } else {
//                            setGraphic(btn)
//                        }
//                    }
//                }
//            }
//        }
//
//        colBtn.setCellFactory(cellFactory)
//
//        IS_stockTable!!.getColumns().add(colBtn)
//
//    }
//
//    private fun addButtonToStockTokenTable() {
//        val colBtn = TableColumn("..")
//
//        val cellFactory = object : Callback<TableColumn<StockToken, Void>, TableCell<StockToken, Void>>() {
//            @Override
//            fun call(param: TableColumn<StockToken, Void>): TableCell<StockToken, Void> {
//                return object : TableCell<StockToken, Void>() {
//
//                    private val btn = Button("Get Dividend Receivable")
//
//                    init {
//                        btn.setOnAction({ event: ActionEvent ->
//                            val st = getTableView().getItems().get(getIndex())
//                            System.out.println("selectedStock: $st")
//                            claimDividendReceivable(st.symbol, st.issuer)
//                            btn.setMinWidth(120)
//
//                        })
//                    }
//
//                    @Override
//                    fun updateItem(item: Void, empty: Boolean) {
//                        super.updateItem(item, empty)
//                        if (empty) {
//                            setGraphic(null)
//                        } else {
//                            setGraphic(btn)
//                        }
//                    }
//                }
//            }
//        }
//
//        colBtn.setCellFactory(cellFactory)
//
//        PORT_stockTable!!.getColumns().add(colBtn)
//
//    }
//
//    private fun addButtonToDRTable() {
//        val colBtn = TableColumn("..")
//
//        val cellFactory = object : Callback<TableColumn<DividendReceivable, Void>, TableCell<DividendReceivable, Void>>() {
//            @Override
//            fun call(param: TableColumn<DividendReceivable, Void>): TableCell<DividendReceivable, Void> {
//                return object : TableCell<DividendReceivable, Void>() {
//
//                    private val btn = Button("Get Payment")
//
//                    init {
//
//                        btn.setOnAction({ event: ActionEvent ->
//                            val dr = getTableView().getItems().get(getIndex())
//                            System.out.println("dr: $dr")
//                            getDividendPayment()
//                            btn.setMinWidth(100)
//
//                        })
//                    }
//
//
//                    @Override
//                    fun updateItem(item: Void, empty: Boolean) {
//                        super.updateItem(item, empty)
//                        if (empty) {
//                            setGraphic(null)
//                        } else {
//                            setGraphic(btn)
//                        }
//                    }
//                }
//            }
//        }
//
//        colBtn.setCellFactory(cellFactory)
//
//        PORT_dividendReceivableTable!!.getColumns().add(colBtn)
//
//    }
//
//    fun retrievePortfolio() {
//        val df = DecimalFormat("#,###,###,##0.00")
//
//        PORT_gbp!!.setText(df.format(getFiatTokenBalance("GBP")))
//        PORT_hkd!!.setText(df.format(getFiatTokenBalance("HKD")))
//        PORT_usd!!.setText(df.format(getFiatTokenBalance("USD")))
//        PORT_sgd!!.setText(df.format(getFiatTokenBalance("SGD")))
//
//        retrieveStock()
//
//        retrieveStockToken()
//
//        retrieveDividendReceivable()
//
//    }
//
//    fun retrievePortfolio(actionEvent: ActionEvent) {
//        retrievePortfolio()
//    }
//
//
//    inner class StockToken(var symbol: String?, var name: String?, var currency: String?, var amount: Long?, var dividend: BigDecimal?, var exDate: String?, var payDate: String?) {
//
//        var issuer: Party? = null
//
//
//        private fun Stock(symbol: String, name: String) {
//            this.symbol = symbol
//            this.name = name
//
//        }
//
//
//        @Override
//        fun toString(): String {
//            return "symbol: $symbol - name: $name"
//        }
//
//
//    }
//
//    inner class DividendReceivable(var issuer: String?, var payDate: String?, var dividendAmount: BigDecimal?, private val isPay: Boolean) {
//
//        fun getIsPay(): Boolean {
//            return isPay
//        }
//
//        fun setIsPay(isPay: Boolean) {
//            var isPay = isPay
//            isPay = isPay
//        }
//    }
//
//    inner class Transaction(var id: String?, var inputs: String?, var outputs: String?, var command: String?, var creationDate: Date?)
//
//    fun IM_submit(actionEvent: ActionEvent) {
//
//
//        //String currency = "";
//
//        System.out.println("IM_submit 1")
//        // showMessage("test");
//        System.out.println(IM_currencyComboBox)
//        System.out.println(IM_currencyComboBox!!.getValue())
//
//        if (IM_currencyComboBox!!.getValue() == null) {
//            System.out.println("IM_submit 1.1")
//            showMessage(" Please input 'Currency'")
//            return
//        }
//
//        val currency = IM_currencyComboBox!!.getValue().toString()
//
//
//        if (IM_amount!!.textProperty().getValue() == null || IM_amount!!.textProperty().getValue().trim().isEmpty()) {
//            showMessage(" Please input 'Issue Amount'")
//            return
//        }
//        val amount = Long(IM_amount!!.textProperty().getValue())
//
//        if (IM_recipient!!.getValue() == null) {
//            showMessage(" Please input 'Recipient'")
//            return
//        }
//        val partyOrganisationName = IM_recipient!!.getValue().toString()
//
//
//        val p = proxy!!.partiesFromName(partyOrganisationName, false).iterator().next()
//        System.out.println("Party=$p")
//
//
//        // IM_receipient.getItems().
//        //FlowHandle<SignedTransaction> flowHandle=
//        try {
//            val flowHandle = proxy!!.startFlowDynamic(com.demo.flow.IssueAndMoveMoney::class.java, currency, amount, p)
//
//            System.out.println(flowHandle.returnValue.get().toString())
//            showMessage("Info: transaction is done with ID=" + flowHandle.returnValue.get().getId())
//            IM_amount!!.textProperty().setValue("")
//
//        } catch (e: Exception) {
//            showMessage(e.getMessage())
//            e.printStackTrace()
//        }
//
//    }
//
//    fun retrieveStock() {
//        System.out.println("retrieveStock")
//
//        val criteria = QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED)
//        System.out.println("getStock1")
//        val (states) = proxy!!.vaultQueryByCriteria(criteria, Stock::class.java)
//        //results.getStates().get().getState().retrieveStock().
//        // int i=0;
//        stockObservableList.clear()
//        for (i in 0 until states.size) {
//            System.out.println(states[i].state.data.maintainers.contains(proxy!!.nodeInfo().legalIdentities[0]))
//            val s = states[i].state.data
//            //my issued stock
//            if (s.maintainers.contains(proxy!!.nodeInfo().legalIdentities[0])) {
//                stockObservableList.add(s)
//            }
//
//        }
//        //Vault.Page<Stock> results = proxy.vaultQuery(Stock.class);
//        //proxy.vaultQuery()
//        System.out.println("getStock2")
//
//
//        //stockObservableList.addAll()
//
//    }
//
//    fun retrieveTransaction() {
//        System.out.println("retrieveTransaction")
//        txnObservableList.clear()
//        val lt = proxy!!.internalVerifiedTransactionsSnapshot()
//        for (i in 0 until lt.size()) {
//            val t = lt[i]
//            System.out.println("ID=" + t.id)
//            System.out.println("Output=" + t.tx.outputStates.toString())
//            System.out.println("Command=" + t.tx.commands.toString())
//            System.out.println("input=" + t.tx.inputs.toString())
//            val txn = Transaction(t.id.toString(), t.tx.inputs.toString(), t.tx.outputStates.toString(), t.tx.commands.toString(), Date())
//            System.out.println("txn=$txn")
//            txnObservableList.add(txn)
//        }
//
//
//        //stockObservableList.addAll()
//
//    }
//
//
//    fun retrieveStockToken() {
//        System.out.println("retrieveStock")
//
//        val criteria = QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED)
//        System.out.println("retrieveStockToken")
//        val (states) = proxy!!.vaultQueryByCriteria(criteria, Stock::class.java)
//        //results.getStates().get().getState().retrieveStock().
//        // int i=0;
//        stockTokenObservableList.clear()
//        for (i in 0 until states.size) {
//            System.out.println(states[i].state.data.maintainers.contains(proxy!!.nodeInfo().legalIdentities[0]))
//            val (symbol, name, currency, dividend, exDate1, payDate1, maintainers) = states[i].state.data
//
//            var exDate = ""
//            if (exDate1 != null) {
//                exDate = SimpleDateFormat("dd/MM/yyyy").format(exDate1)
//            }
//            var payDate = ""
//            if (payDate1 != null) {
//                payDate = SimpleDateFormat("dd/MM/yyyy").format(payDate1)
//            }
//            val amount = getStockTokenBalance(symbol)
//            if (amount > 0) { //only put into portf if amount > 0. Your ledger having the stock info doesn't mean you have the token
//                //be careful, i assume the maintainer is the issuer.
//                val st = StockToken(symbol, name, currency, getStockTokenBalance(symbol), dividend, exDate, payDate)
//
//                System.out.println("Issuer=$maintainers")
//                st.issuer = maintainers[0]
//                System.out.println("st=$st")
//                stockTokenObservableList.add(st)
//            }
//            //my issued stock
//            //if (s.getMaintainers().contains(proxy.nodeInfo().getLegalIdentities().get(0))){
//
//            //}
//
//        }
//        //Vault.Page<Stock> results = proxy.vaultQuery(Stock.class);
//        //proxy.vaultQuery()
//        System.out.println("retrieveStockToken")
//
//
//        //stockObservableList.addAll()
//
//    }
//
//    fun retrieveDividendReceivable() {
//        System.out.println("retrieveDividendReceivable")
//
//        val criteria = QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED)
//        System.out.println("retrieveDividendReceivable")
//        val results = proxy!!.vaultQueryByCriteria<DividendReceivableState>(criteria, DividendReceivableState::class.java!!)
//        //results.getStates().get().getState().retrieveStock().
//        // int i=0;
//        drObservableList.clear()
//        for (i in 0 until results.states.size) {
//
//            val dr = results.states.get(i).state.data
//            System.out.println(dr.toString())
//            val da = BigDecimal(dr.getDividendAmount().getDisplayTokenSize().floatValue() * dr.getDividendAmount().getQuantity())
//
//            /*String isPay = "NO";
//            if (dr.isPay()){
//                isPay = "YES";
//            }*/
//            val drUI = DividendReceivable(dr.getIssuer().getName().getOrganisation(), SimpleDateFormat("dd/MM/yyyy").format(dr.getPayDate()), da, dr.isPay())
//            if (dr.getHolder().equals(proxy!!.nodeInfo().legalIdentities[0])) {
//                drObservableList.add(drUI)
//            }
//
//
//        }
//
//
//    }
//
//    fun getFiatTokenBalance(currency: String): Float {
//        try {
//            val flowHandle = proxy!!.startFlowDynamic(com.demo.flow.getFiatTokenBalance::class.java, currency)
//
//            System.out.println(flowHandle.returnValue.get().toString())
//            //showMessage("Info: transaction is done with ID=" +flowHandle.getReturnValue().get().getId() );
//            val q = flowHandle.returnValue.get().getQuantity()
//            val d = flowHandle.returnValue.get().getDisplayTokenSize()
//
//
//            return q.floatValue() * d.floatValue()
//
//            //ystem.out.println(flowHandle.getReturnValue().get().getDisplayTokenSize());
//            //System.out.println(flowHandle.getReturnValue().get().getQuantity());
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//            showMessage(e.getMessage())
//        }
//
//        return 0f
//    }
//
//    fun getStockTokenBalance(symbol: String): Long {
//        try {
//            val flowHandle = proxy!!.startFlowDynamic(com.demo.flow.getStockTokenBalance::class.java, symbol)
//
//            System.out.println(flowHandle.returnValue.get().toString())
//            //showMessage("Info: transaction is done with ID=" +flowHandle.getReturnValue().get().getId() );
//            return flowHandle.returnValue.get().getQuantity()
//            //BigDecimal d =flowHandle.getReturnValue().get().getDisplayTokenSize();
//
//
//            // return ;
//
//            //ystem.out.println(flowHandle.getReturnValue().get().getDisplayTokenSize());
//            //System.out.println(flowHandle.getReturnValue().get().getQuantity());
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//            showMessage(e.getMessage())
//        }
//
//        return 0
//    }
//
//
//    fun showMessage(message: String) {
//        val dialog = Stage()
//        dialog.initModality(Modality.APPLICATION_MODAL)
//        dialog.initOwner(primaryStage)
//        dialog.setTitle("Info")
//
//
//        val dialogVbox = VBox(20)
//
//        dialogVbox.getChildren().add(Text(message))
//
//        dialogVbox.setAlignment(Pos.CENTER)
//
//
//        val dialogScene = Scene(dialogVbox, 700, 200)
//        dialog.setScene(dialogScene)
//        dialog.show()
//    }
//
//    fun IS_issueStock(actionEvent: ActionEvent) {
//        System.out.println("IS_submit 1")
//        // showMessage("test");
//        //System.out.println(IS_symbol);
//        // System.out.println(IM_currencyComboBox.getValue());
//
//        if (IS_currency!!.getValue() == null) {
//            System.out.println("IS_submit 1.1")
//            showMessage(" Please input 'Trading Currency'")
//            return
//        }
//
//        val currency = IS_currency!!.getValue().toString()
//        System.out.println("currency=$currency")
//
//        if (IS_symbol!!.textProperty().getValue().trim().isEmpty()) {
//            showMessage(" Please input 'Symbol'")
//            return
//        }
//        if (IS_name!!.textProperty().getValue().trim().isEmpty()) {
//            showMessage(" Please input 'Securities Name'")
//            return
//        }
//
//        if (IS_issueVol!!.textProperty().getValue().trim().isEmpty()) {
//            showMessage(" Please input 'Token Volume' to be issued")
//            return
//        }
//
//        val issueVol = Integer(IS_issueVol!!.textProperty().getValue())
//
//        System.out.println("symbol=" + IS_symbol!!.textProperty().getValue().trim())
//        System.out.println("IS_name=" + IS_name!!.textProperty().getValue().trim())
//        System.out.println("issueVol=$issueVol")
//
//        //FlowHandle<SignedTransaction> flowHandle=
//        try {
//            val flowHandle = proxy!!.startFlowDynamic(com.demo.flow.IssueStock::class.java,
//                    IS_symbol!!.textProperty().getValue().trim(),
//                    IS_name!!.textProperty().getValue().trim(),
//                    currency,
//                    issueVol
//            )
//
//            System.out.println(flowHandle.returnValue.get().toString())
//            showMessage("Transaction is done with ID=" + flowHandle.returnValue.get().getId())
//            IS_symbol!!.textProperty().setValue("")
//            IS_name!!.textProperty().setValue("")
//            IS_issueVol!!.textProperty().setValue("")
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//            showMessage(e.getMessage())
//        }
//
//        retrieveStock()
//    }
//
//    fun IS_announceDivided(actionEvent: ActionEvent) {
//
//        System.out.println("IS_submit 1")
//        // showMessage("test");
//        //System.out.println(IS_symbol);
//        // System.out.println(IM_currencyComboBox.getValue());
//
//
//        val currency = IS_currency!!.getValue().toString()
//        System.out.println("currency=$currency")
//
//        if (IS_symbol!!.textProperty().getValue().trim().isEmpty()) {
//            showMessage(" Please input 'Symbol'")
//            return
//        }
//
//
//        if (IS_dividend!!.textProperty().getValue().trim().isEmpty()) {
//            showMessage(" Please input dividend")
//            return
//        }
//        val dividend = BigDecimal(IS_dividend!!.textProperty().getValue())
//
//
//        val exDate: java.util.Date
//        try {
//            System.out.println(IS_exDate!!.textProperty().getValue().toString())
//            exDate = SimpleDateFormat("dd/MM/yyyy").parse(IS_exDate!!.textProperty().getValue().toString())
//        } catch (e: Exception) {
//            showMessage(e.getMessage())
//            return
//        }
//
//        val payDate: Date
//        try {
//            payDate = SimpleDateFormat("dd/MM/yyyy").parse(IS_payDate!!.textProperty().getValue().toString())
//        } catch (e: Exception) {
//            showMessage(e.getMessage())
//            return
//        }
//
//
//
//        System.out.println("symbol=" + IS_symbol!!.textProperty().getValue().trim())
//
//
//        //FlowHandle<SignedTransaction> flowHandle=
//        try {
//
//            val flowHandle = proxy!!.startFlowDynamic(com.demo.flow.AnnounceDividend::class.java,
//                    IS_symbol!!.textProperty().getValue().trim(),
//                    dividend,
//                    exDate, payDate
//            )
//
//            System.out.println(flowHandle.returnValue.get().toString())
//            showMessage("Transaction is done with ID=" + flowHandle.returnValue.get().getId())
//            IS_symbol!!.textProperty().setValue("")
//            IS_name!!.textProperty().setValue("")
//            IS_issueVol!!.textProperty().setValue("")
//            IS_dividend!!.textProperty().setValue("")
//            IS_exDate!!.textProperty().setValue("")
//            IS_payDate!!.textProperty().setValue("")
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//            showMessage(e.getMessage())
//        }
//
//        retrieveStock()
//
//    }
//
//    fun claimDividendReceivable(symbol: String?, issuer: Party?) {
//
//        System.out.println("claimDividendReceivable 1")
//
//
//        //FlowHandle<SignedTransaction> flowHandle=
//
//        // FlowHandle<SignedTransaction> flowHandle= proxy.startFlowDynamic(com.demo.flow.MoveMoney.class, currency, amount, p);
//
//        try {
//            val flowHandle = proxy!!.startFlowDynamic(com.demo.flow.ClaimDividendReceivable::class.java, symbol, issuer)
//
//            System.out.println(flowHandle.returnValue.get().toString())
//            showMessage("Transaction is done with ID=" + flowHandle.returnValue.get().getId())
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//            showMessage(e.getMessage())
//        }
//
//        retrievePortfolio()
//
//    }
//
//    fun getDividendPayment() {
//
//        System.out.println("    public void getDividendPayment() {\n 1")
//
//
//        //FlowHandle<SignedTransaction> flowHandle=
//
//
//        // FlowHandle<SignedTransaction> flowHandle= proxy.startFlowDynamic(com.demo.flow.MoveMoney.class, currency, amount, p);
//
//        try {
//            val flowHandle = proxy!!.startFlowDynamic<String>(com.demo.flow.GetDividendPayment::class.java)
//            System.out.println(flowHandle.returnValue.get().toString())
//            showMessage("Transaction is done")
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//            showMessage(e.getMessage())
//        }
//
//        retrievePortfolio()
//
//    }
//
//    fun MF_submit(actionEvent: ActionEvent) {
//        //String currency = "";
//
//        System.out.println("MF_submit 1")
//        // showMessage("test");
//
//        if (MF_currencyComboBox!!.getValue() == null) {
//            System.out.println("IM_submit 1.1")
//            showMessage(" Please input 'Currency'")
//            return
//        }
//
//        val currency = MF_currencyComboBox!!.getValue().toString()
//
//
//        if (MF_amount!!.textProperty().getValue() == null || MF_amount!!.textProperty().getValue().trim().isEmpty()) {
//            showMessage(" Please input 'Transfer Amount'")
//            return
//        }
//        val amount = Long(MF_amount!!.textProperty().getValue())
//
//        if (MF_recipient!!.getValue() == null) {
//            showMessage(" Please input 'Recipient'")
//            return
//        }
//        val partyOrganisationName = MF_recipient!!.getValue().toString()
//        val p = proxy!!.partiesFromName(partyOrganisationName, false).iterator().next()
//        System.out.println("Party=$p")
//
//
//        // IM_receipient.getItems().
//        //FlowHandle<SignedTransaction> flowHandle=
//
//        try {
//
//            val flowHandle = proxy!!.startFlowDynamic(com.demo.flow.MoveMoney::class.java, currency, amount, p)
//
//            System.out.println(flowHandle.returnValue.get().toString())
//            showMessage("Info: transaction is done with ID=" + flowHandle.returnValue.get().getId())
//            MF_amount!!.textProperty().setValue("")
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//            showMessage(e.getMessage())
//        }
//
//    }
//
//    fun MS_submit(actionEvent: ActionEvent) {
//        System.out.println("MF_submit 1")
//        // showMessage("test");
//
//        if (MS_symbol!!.textProperty().getValue() == null) {
//            System.out.println("IM_submit 1.1")
//            showMessage(" Please input 'symbol'")
//            return
//        }
//
//
//
//
//        if (MS_amount!!.textProperty().getValue() == null || MS_amount!!.textProperty().getValue().trim().isEmpty()) {
//            showMessage(" Please input 'Transfer Amount'")
//            return
//        }
//        val amount = Long(MS_amount!!.textProperty().getValue())
//
//        if (MS_recipient!!.getValue() == null) {
//            showMessage(" Please input 'Recipient'")
//            return
//        }
//        val partyOrganisationName = MS_recipient!!.getValue().toString()
//        val p = proxy!!.partiesFromName(partyOrganisationName, false).iterator().next()
//        System.out.println("Party=$p")
//
//
//        // IM_receipient.getItems().
//        //FlowHandle<SignedTransaction> flowHandle=
//        try {
//            val flowHandle = proxy!!.startFlowDynamic(com.demo.flow.MoveStock::class.java, MS_symbol!!.textProperty().getValue().trim(), amount, p)
//
//            System.out.println(flowHandle.returnValue.get().toString())
//            showMessage("Info: transaction is done with ID=" + flowHandle.returnValue.get().getId())
//            MS_amount!!.textProperty().setValue("")
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//            showMessage(e.getMessage())
//        }
//
//    }
//}
