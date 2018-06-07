<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv="x-ua-compatible" content="ie=edge">
    <meta name="google" value="notranslate">
    <title>Payment systems integration</title>
    <script src="http://code.jquery.com/jquery-1.8.3.js"></script>
    <script src="https://www.paypalobjects.com/js/external/api.js"></script>
    <style>
        .card__body {
            padding: 20px;
        }

        .card {
            background-color: #fff;
            border: 1px solid rgba(180, 180, 180, .5);
            border-radius: 5px;
            box-shadow: -2px 0 5px rgba(0, 0, 0, .11);
        }

        .connect-button {
            display: inline-block;
            margin: 5px 5px;
            background-image: -webkit-linear-gradient(#28a0e5, #5fbaef);
            background-image: linear-gradient(#28a0e5, #5fbaef);
            -webkit-font-smoothing: antialiased;
            color: #FFFFFF;
            font-size: 14px;
            font-weight: 700;
            border: 0;
            padding: 2px 10px;
            height: 32px;
            border-radius: 4px;
            box-shadow: 0 1px 0 rgba(0, 0, 0, .2);
            cursor: pointer;
            -moz-user-select: none;
            -webkit-user-select: none;
            -ms-user-select: none;
            user-select: none;
            text-decoration: none !important;
        }

        .form__item {
            margin-bottom: 10px;
        }

        .form__item label {
            display: inline-block;
            max-width: 100%;
            margin-bottom: 5px;
            font-weight: 700;
        }

        .control {
            font-size: 0;
            width: 100%;
        }

        .control__box {
            background-color: #fff;
            border: 1px solid rgba(180, 180, 180, .5);
            border-radius: 3px;
            color: #343f4b;
            font-size: 16px;
            height: 40px;
            max-width: 100%;
            overflow: hidden;
            padding-left: 10px;
            padding-right: 10px;
            text-overflow: ellipsis;
            transition: all .3s ease;
            width: 100%;
            -webkit-transition: all .3s ease;
        }

        .control__box::-webkit-input-placeholder {
            color: #aeb2b7;
        }

        .control__box::-moz-placeholder {
            color: #aeb2b7;
        }

        .control__box:-moz-placeholder {
            color: #aeb2b7;
        }

        .control__box:-ms-input-placeholder {
            color: #aeb2b7;
        }

        .control__box:hover {
            border-color: #b0dbea;
        }

        .control__box:focus {
            border-color: #4baed0;
        }

        .billing__item {
            align-items: flex-start;
            display: -webkit-box;
            display: -ms-flexbox;
            display: flex;
            justify-content: space-between;
            -webkit-box-align: start;
            -webkit-box-pack: justify;
            -ms-flex-align: start;
            -ms-flex-pack: justify;
        }

        .billing__name {
            width: 42%;
        }

        .billing__info {
            width: 50%;
        }

        .billing__title {
            color: #444d67;
            font-size: 21px;
            margin-bottom: 10px;
        }

        .billing__text {
            color: #7c818e;
            font-size: 14px;
        }

        .billing__btn {
            background-color: #38b549;
            border: 1px solid #38b549;
            color: #fff;
        }

        .billing__btn:hover {
            background-color: #31aff5;
            border: 1px solid #31aff5;
        }

        .billing__plan-name {
            color: #666;
            font-weight: 600;
        }

        .billing__box {
            background-color: rgba(0, 188, 212, .08);
            border: 1px solid rgba(204, 204, 204, .18);
            border-radius: 2px;
            margin-bottom: 35px;
            padding: 24px;
            text-align: center;
            transition: background-color .9s ease;
        }

        .billing__box:hover {
            background-color: #f9f9f9;
            cursor: default;
        }

        .billing__small {
            color: #7c818e;
            font-size: 14px;
            margin-left: 2px;
            margin-right: 7px;
        }

        .billing__action-card {
            align-items: center;
            display: -webkit-box;
            display: -ms-flexbox;
            display: flex;
            flex-wrap: wrap;
            justify-content: space-between;
            -webkit-box-align: center;
            -webkit-box-pack: justify;
            -ms-flex-align: center;
            -ms-flex-pack: justify;
            -ms-flex-wrap: wrap;
        }

        .billing__action {
            display: -webkit-box;
            display: -ms-flexbox;
            display: flex;
            justify-content: center;
            margin-top: 45px;
            -webkit-box-pack: center;
            -ms-flex-pack: center;
        }

        .billing__card {
            letter-spacing: 4px;
        }

        .btn-saas {
            align-self: center;
            border-radius: 4px;
            cursor: pointer;
            display: -webkit-box;
            display: -webkit-flex;
            display: -ms-flexbox;
            display: flex;
            font-size: 16px;
            font-weight: 600;
            justify-content: center;
            letter-spacing: .2px;
            line-height: 1.5;
            max-width: 180px;
            min-width: 190px;
            padding: 12px 20px;
            position: relative;
            text-align: center;
            text-decoration: none;
            text-transform: none;
            transition: .2s ease-in-out;
            vertical-align: middle;
            white-space: nowrap;
            width: 100%;
            z-index: 1;
            -webkit-align-self: center;
            -webkit-box-pack: center;
            -ms-flex-item-align: center;
            -ms-flex-pack: center;
            -webkit-justify-content: center;
            -ms-touch-action: manipulation;
            touch-action: manipulation;
            -webkit-transition: .2s ease-in-out;
            -webkit-user-select: none;
            -moz-user-select: none;
            -ms-user-select: none;
            user-select: none;
        }
    </style>
</head>
<body>
<div class="card__body">
<#--Stripe start-->
    <div class="js-integration">
        <span> Stripe payment integration</span>

        <div class="card js-integration-card">
            <div class="card__body">
                <button class="connect-button js-integration-button">
                    <a href="https://connect.stripe.com/oauth/authorize?response_type=code&amp;client_id=${stripeClientId}&amp;scope=read_write">
                        Connect with Stripe</a>
                </button>
                <#if !(billingUser.stripeAccount??) || billingUser.stripeAccount=="">
                    <button id="create-stripe-account"
                            class="connect-button js-integration-button"><span>I don't have Stripe account</span>
                    </button>
                </#if>
                <br>
                Account id
                <div class="form__item">
                    <div class="control">
                        <input id="stripe-accountid"
                               placeholder="Connect your account in stripe.com"
                               class="control__box" type="text"
                               value="${billingUser.stripeAccount!""}">
                    </div>
                </div>
            </div>
        </div>
    </div>
<#--Paypal-->
    <div class="card__body js-integration">
        <span> PayPal payment integration</span>

        <div class="card js-integration-card">
            <div class="card__body">
                <span id='lippButton'></span>
                <br>
                <br>
                Connected account
                <div class="form__item">
                    <div class="control">
                        <input id="paypal-accountid"
                               placeholder="Connect your PayPal account"
                               class="control__box" type="text"
                               value="${billingUser.paypalAccount!""}">
                    </div>
                </div>
            </div>
        </div>
    </div>

    <section class="billing__item">
        <div class="billing__name">
            <h3 class="billing__title">Active plan</h3>

            <p class="billing__text">Your chosen plan</p>
        </div>

        <div class="billing__info">
            <div class="billing__plan">
                <div class="billing__plan-name billing__box">
                                    <#if tariff??>
                                        ${tariff.extendedTitle}
                                    <#else>
                                        No plan
                                    </#if>
                </div>
                    <#if subscription??>
                        <div class="billing__action-card">
                            <span id="js-change-plan"
                                  class="btn-saas billing__btn">
                                <#if tariff??>
                                    Change
                                <#else>
                                    Choose
                                </#if>plan
                            </span>
                            <#if expired?? && expired>
                                <#if tariff?? && card??>
                                    <button type="button" id="retryUnpaid" class="btn-saas billing__btn">
                                        Retry
                                        payment
                                    </button>
                                </#if>
                                Your account expired!
                            <#else>
                                <#if subscription??>
                                    Expire
                                    date: ${timeAgoUtil.leftDuration(subscription.currentPeriodEnd*1000-now)}
                                </#if>
                            </#if>
                        </div>
                    </#if>
            </div>
        </div>
    </section>
    <#if subscription??>
        <hr class="hr-ui">
        <section class="billing__item">
            <div class="billing__name">
                <h3 class="billing__title">Payment details</h3>

                <p class="billing__text">Information your card.</p>
            </div>
            <div class="billing__info">
                <div data-card-id="${(card.id)!""}" class="billing__card billing__box"><#if card??>****
                    ****
                    **** ${card.last4}<#else>No
                    card</#if></div>
                <div class="billing__action-card">
                    <button type="button" id="changeCardBtn" class="btn-saas billing__btn">
                        <#if card??>Change
                        <#else>Add
                        </#if>
                        billing card
                    </button>
                    <#if card??>
                        <button type="button" id="deleteCard" class="btn-saas billing__btn">Delete
                        </button>
                    </#if>
                </div>
            </div>
        </section>
    </#if>

</body>
<script>
    $(document).ready(function () {
        paypal.use(['login'], function (login) {
            login.render({
                "appid": "${paypalClientId}",
            <#if paypalMode == '${paypalMode}'>
                "authend": "${paypalMode}",
            </#if>
                "scopes": "openid email",
                "containerid": "lippButton",
                "locale": "en-us",
                "returnurl": "${mainDomain}/paypal/oauth?state=${paypalClientId}"
            });
        });

        $(document).on('click', '#create-stripe-account', function (e) {
            e.preventDefault();
            $.ajax({
                url: "${mainDomain}/saas/integrations/stripe",
                type: "POST",
                success: function (result) {
                    var email = result.responseData;
                    // notify('Account created.', 'Please check mailbox of owner account (' + email + ')');
                    setTimeout(function () {
                        location.reload();
                    }, 1500);
                }, statusCode: {
                    400: function () {
                        // notify('Error', 'Hotel owner already have Stripe account');
                    },
                    500: function () {
                        // notify('Error', 'Failed to create Stripe account');
                    }
                }
            });
        });
    });
</script>
</html>