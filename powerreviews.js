(function (app, $) {
    app.powerreviews = {
        readReviews: function () {
            $('#pdpReviewsTabInput').next('label').trigger('click');
            $('html, body').animate({
                scrollTop: $('#pdpReviewsTab').offset().top
            }, 500);
        },
        readQuestions: function () {
            $('#pdpQuestionsTabInput').next('label').trigger('click');
            $('html, body').animate({
                scrollTop: $('#pdpQuestionsTab').offset().top
            }, 500);
        },
        // These functions dynamically capture current Product ID and construct URLs to Review and QA Pipelines.
        redirectReview: function (url) {
            window.location.replace(url + '?pid=' + $('#pid').val());
        },
        redirectQA: function (url) {
            window.location.replace(url + '&pid=' + $('#pid').val());
        },
        // method called after the reviews display and snippets have been called on a pdp
        onRender: function (config, data, callback) {
            if (callback !== null) {
                callback(config, data);
            }

        },
        // method called after the write a review page has been submitted.
        onSubmit: function (config, data, callback) {
            if (callback !== null) {
                callback(config, data);
            }
        },
        render: function () { //sends off tiles
            var tiles = document.querySelectorAll('[data-pwr-itemid]');
            var tilesRendered = [];
            var prStruct = [];
            var tile;
            var i;
            var pageIdValue;
            var uniqueId;
            var reviewObj;
            $('[data-pwr-itemid]').each(function(i, ele){
                if ($(ele).html() !== "") {
                    tilesRendered.push(ele);
                }
            });
            /*
             * Checks for tiles rendered on grid pages and recommendations.
             * Fires one time for all tiles
             */
            for (i = 0; i < tiles.length; i++) {
                tile = tiles[i];
                if (tilesRendered.indexOf(tile) !== -1) {
                    continue; // eslint-disable-line
                }
                pageIdValue = tile.attributes['data-pwr-itemid'].value;
                uniqueId = 'category-snippet-'.concat(Math.random().toString(36).substr(2, 16));
                tile.attributes.id.value = uniqueId;

                if (tile.attributes['data-pwr-itemid'] && tile.attributes['data-pwr-itemid'].value) {
                    reviewObj = mergeObjects({}, window.POWER_REVIEWS_CONFIG, {
                        page_id: pageIdValue,
                        ENABLE_CLIENT_SIDE_STRUCTURED_DATA: false,
                        components: {
                            CategorySnippet: uniqueId
                        }
                    });
                    if (tile.className.indexOf('pwr-pdp') !== -1) {
                        reviewObj.components = {
                            ReviewSnippet: uniqueId
                        };
                    }

                    prStruct.push(reviewObj);
                }
                tilesRendered.push(tile);
            }

            /*
             * Checks for the review display or snippet on the pdp, then checks for a
             * "power_product" object, which is created on product pages. Product Set pages do not
             * have this variable initialized.
             */
            if (($('#pr-reviewsnippet').length || $('#pr-reviewdisplay').length)) {
                if ('power_product' in window) {
                    var components = {};
                    if ($("#pr-reviewsnippet").html() === "") {
                        components.ReviewSnippet = 'pr-reviewsnippet';
                    }
                    if ($("#pr-reviewdisplay").html() === "") {
                        components.ReviewDisplay = 'pr-reviewdisplay';
                    }
                    if ($("#pr-questiondisplay").html() === "") {
                        components.QuestionDisplay = 'pr-questiondisplay';
                    }
                    reviewObj = mergeObjects({}, window.POWER_REVIEWS_CONFIG, {
                        product: power_product,
                        page_id: power_productId,
                        structured_data_product_id: power_productId,
                        review_wrapper_url: power_write_url,
                        REVIEW_DISPLAY_SNAPSHOT_TYPE: 'SIMPLE',
                        components: components,
                        ENABLE_CLIENT_SIDE_STRUCTURED_DATA: true,
                        // REVIEW_DISPLAY_PAGINATION_TYPE:'VERTICAL',
                        // REVIEW_DISPLAY_LIST_TYPE:'CONDENSED',
                        on_render: function (config, data) {
                            app.powerreviews.onRender(config, data, function (config, data) {
                                if (config.component === 'ReviewSnapshot') {
                                    var e = document.createElement('script');
                                    e.type = 'application/ld+json';
                                    e.id = 'product-schema';
                                    if (data.average_rating && data.review_count) {
                                        var aggregateRating = {};
                                        aggregateRating['@context'] = "http://schema.org/";
                                        aggregateRating['@type'] = 'AggregateRating';
                                        aggregateRating['ratingValue'] = data.average_rating;
                                        aggregateRating['reviewCount'] = data.review_count;
                                    }
                                    if (power_product.price && power_product.maxPrice && power_product.maxPrice > power_product.price) {
                                        var offers = {
                                            '@type': "AggregateOffer",
                                            'lowPrice': power_product.price,
                                            'highPrice': power_product.maxPrice,
                                            'availability': power_product.in_stock ? "http://schema.org/InStock" : "http://schema.org/OutOfStock",
                                            'priceCurrency': "USD"
                                        }
                                    } else {
                                        var offers = {
                                            '@type': 'Offer',
                                            'priceCurrency': 'USD',
                                            'availability': power_product.in_stock ? "http://schema.org/InStock" : "http://schema.org/OutOfStock",
                                            'price': power_product.price
                                        }
                                    }
                                    productSchema = {
                                        '@context': 'http://schema.org/',
                                        '@type': 'Product',
                                        'sku': power_productId,
                                        'brand': power_product.brand_name ? power_product.brand_name : '',
                                        'name': power_product.name ? power_product.name : '',
                                        'image': power_product.image_url ? power_product.image_url : '',
                                        'description': power_product.description ? power_product.description : '',
                                        'url': power_product.url,
                                        'upc': power_product.upc ? power_product.upc : '',
                                        offers
                                    };
                                    if (aggregateRating) {
                                        productSchema.aggregateRating = aggregateRating;
                                    }
                                    // Get the reviewes schema from the dom because powerReviews doesn't pass it in the callback smh
                                    if ($('.pr-review-display script[type="application/ld+json"]').length) {
                                        var reviewsSchema = $('.pr-review-display script[type="application/ld+json"]')[0].innerHTML;
                                        var reviewsSchemaJSON = JSON.parse(reviewsSchema);
                                        // Remove itemReviewed property from the schema because the reviewes will live under the product
                                        reviewsSchemaJSON['@graph'].forEach(function(v){ delete v.itemReviewed });
                                        // Link the reviews list to the product schema
                                        productSchema.review = reviewsSchemaJSON['@graph'];
                                    }
                                    e.innerHTML = JSON.stringify(productSchema);
                                    var t = document.getElementsByTagName('script')[0];
                                    t.parentNode.insertBefore(e, t);
                                    // Remove Snippets populated by PowerReviews
                                    $('.pr-review-display script[type="application/ld+json"]').remove();
                                }
                                $('.pr-review-snippet-container script[type="application/ld+json"]').remove();
                            });
                        }
                    });
                    prStruct.push(reviewObj);
                }
            }

            /*
             * Checks for the pr-write div and renders the write a review form
             * after DOM completion.
             */
            if ($('#pr-write').length) {

                reviewObj = mergeObjects({}, window.POWER_REVIEWS_CONFIG, {
                    product: power_product,
                    page_id: power_productId,
                    structured_data_product_id: power_productId,
                    components: {
                        Write: 'pr-write'
                    }
                });
                prStruct = reviewObj;
            }

            /* global POWERREVIEWS */
            if (POWERREVIEWS) {
                //sends the struct on time per page load.
                POWERREVIEWS.display.render(prStruct);
            }
        }
    };


    (function () {
        /*
         * Waits until the DOM is loaded so all available tiles, snippets, and display divs
         * have been loaded. Also gives time for the power reviews js files to load before
         * trying to call on them.
         */
        $(document).ready(function () {
            var refreshPR = setInterval(function(){
                var tilesReviewsNotLoaded = false;
                $('[data-pwr-itemid]').each(function(i, ele){
                    if ($(ele).html() === "") {
                        tilesReviewsNotLoaded = true;
                    }
                });
                if ($("#pr-reviewdisplay").html() === "" || $("#pr-reviewsnippet").html() === "" ||
                    $("#pr-questiondisplay").html() === "" || $('#pr-write').html() === "" || tilesReviewsNotLoaded) {
                    app.powerreviews.render();
                } else {
                    clearInterval(refreshPR);
                }
            }, 1000);
            window.PWR_RENDER = debounce(app.powerreviews.render, 50);
            addDOMEvents();
        });
    })();

    var addDOMEvents = function () {
        // Tabs fix correct working
        if ($('#pdpMain').length) {
            $('#pdpMain').find('.pr-tabs .tabs .tab').first().find('.tab-label').trigger('click');
            $('#pdpMain').on('click', '.tab-label', function () {
                var tabHeight = $(this).next('.tab-content').outerHeight() + $(this).outerHeight();

                $(this).closest('.product-info').outerHeight(tabHeight);
                $('.tab-content').hide(10);
                $(this).next('.tab-content').fadeIn(10);
                $(window).trigger('scroll');
            });
            $('#pdpMain').on('click', '.prPaClicker', function () {
                var tabHeight = $(this).closest('#pdpQuestionsTab').outerHeight() + $(this).closest('.tab').outerHeight();

                $(this).closest('.product-info').outerHeight(tabHeight);
            });
            $('#pdpMain').on('click', '.pr-review-helpful-text-link', function () {
                var tabHeight = $(this).closest('#pdpReviewsTab').outerHeight() + $(this).closest('.tab').outerHeight();

                $(this).closest('.product-info').outerHeight(tabHeight);
            });
        }
    };

    var mergeObjects = function mergeObjects() {
        var resObj = {};
        var j;
        var obj;
        var keys;

        for (var x = 0; x < arguments.length; x += 1) {
            obj = arguments[x];
            keys = Object.keys(obj);

            for (j = 0; j < keys.length; j += 1) {
                resObj[keys[j]] = obj[keys[j]];
            }
        }
        return resObj;
    };

    var debounce = function (func, wait) {
        var timeout;
        return function () {
            var context = this;
            var args = arguments;
            var later = function () {
                timeout = null;
                func.apply(context, args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    };
}(window.app = window.app || {}, jQuery));
