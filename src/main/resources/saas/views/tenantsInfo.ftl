<#-- @ftlvariable name="tenants" type="java.util.List<kpfu.itis.dto.TenantDto>" -->
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv="x-ua-compatible" content="ie=edge">
    <meta name="google" value="notranslate">
    <title>Subscribers info</title>
    <style>
        .headline {
            align-items: center;
            display: -webkit-box;
            display: -webkit-flex;
            display: -ms-flexbox;
            display: flex;
            font-size: 16px;
            justify-content: space-between;
            margin-bottom: 30px;
            -webkit-align-items: center;
            -webkit-box-align: center;
            -webkit-box-pack: justify;
            -ms-flex-align: center;
            -ms-flex-pack: justify;
            -webkit-justify-content: space-between;
        }

        .title {
            font-size: 18px;
            font-weight: 700;
            max-width: 100%;
            overflow: hidden;
            text-overflow: ellipsis;
        }

        .card {
            background-color: #fff;
            border: 1px solid rgba(180, 180, 180, .5);
            border-radius: 5px;
            box-shadow: -2px 0 5px rgba(0, 0, 0, .11);
        }

        .card__body {
            padding: 20px;
        }

        .card__body .table {
            margin-left: -20px;
            margin-right: -20px;
        }

        .table {
            overflow-x: auto;
            text-align: center;
            transition: padding-bottom .3s ease;
            -webkit-transition: padding-bottom .3s ease;
        }

        .table__container {
            position: relative;
            width: 100%;
        }

        .table td,
        .table th {
            line-height: 1.2;
            padding: 10px 15px;
            vertical-align: middle;
        }

        .table td img,
        .table th img {
            display: block;
            height: auto;
            margin: auto;
            max-width: 50px;
            min-width: 30px;
        }

        .table__head {
            border-bottom: 1px solid #cfcfcf;
            font-weight: 600;
            line-height: 1.2;
        }

        .table__head th,
        .table__head td {
            padding-bottom: 15px;
        }

        .table__head + .table__body:before {
            content: '';
            display: block;
            height: 10px;
        }

        .table__body tr {
            transition: background-color .2s ease;
            -webkit-transition: background-color .2s ease;
        }

        .table__body tr:hover {
            background-color: rgba(52, 63, 75, .1) !important;
        }

        .table__body tr:hover .toggle {
            background-color: #fff;
        }

        .table__body tr[data-href],
        .table__body tr[data-popup],
        .table__body tr[data-table-popup] {
            cursor: pointer;
        }

    </style>
</head>
<body>
<div class="headline">
    <h2 class="title">Subscribers</h2>
</div>
<div class="card">
    <div class="card__body">
        <div class="table">
            <table class="table__container">
                <thead class="table__head">
                <tr>
                    <td>Subscriber name</td>
                    <td>Email</td>
                    <td>Plan</td>
                    <td>Subscription end</td>
                    <td>PayPal account</td>
                    <td>Stripe account</td>
                    <td>Card</td>
                    <td>Card status</td>
                </tr>
                </thead>
                <tbody class="table__body">
        <#list tenants as tenant>
        <tr>
                <#if tenant.billingUser??>
                    <td>${tenant.billingUser.name!""}</td>
                    <td>${tenant.billingUser.email!""}</td>
                    <td><#if tenant.plan??>${tenant.plan.title!""}</#if></td>
                    <td><#if tenant.currentPeriodEnd??>${tenant.currentPeriodEnd?number_to_date?string}</#if></td>
                    <td>${tenant.billingUser.paypalAccount!""}</td>
                    <td>${tenant.billingUser.stripeAccount!""}</td>
                <#else>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td></td>
                </#if>
                <#if tenant.card??>
                    <td>${tenant.card.last4!""}</td>
                    <td>${tenant.card.status!""}</td>
                <#else>
                    <td></td>
                    <td></td>
                </#if>
        </tr>
        </#list>
                </tbody>
            </table>
        </div>
    </div>
</div>
</body>
</html>