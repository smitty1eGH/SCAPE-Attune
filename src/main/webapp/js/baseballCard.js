var starDisplay = '<fieldset class="rating">' +
        '<legend>Please rate:</legend>' +
        '<input type="radio" id="star5" name="rating" value="5" /><label for="star5" title="Great"> </label>' +
        '<input type="radio" id="star4" name="rating" value="4" /><label for="star4" title="Good"> </label>' +
        '<input type="radio" id="star3" name="rating" value="3" /><label for="star3" title="OK"> </label>' +
        '<input type="radio" id="star2" name="rating" value="2" /><label for="star2" title="Not Good"> </label>' +
        '<input type="radio" id="star1" name="rating" value="1" /><label for="star1" title="Terrible"> </label>' +
    '</fieldset>';

var baseballCard = {
    createUserRatingDisplay : function(user, rating) {
        var output = '';
        output += '<li class="row-lg-12 ratingsDisplay">';

        var ratingIndex = 0;
        var buttonText = 'Submit';

        var score = 0;
        var title = '';
        var comment = '';
        var legend = "Review this:";

        console.log(rating);
        if(typeof rating !== 'undefined') {
            buttonText = 'Update';
            score = rating.rating;
            title = ' value="' + rating.title + '"';
            comment = rating.comment;
            legend = "Your review:";
        }

        extraHTML = '<div class="form-group titleDiv">' +
                    //'<label for="titleText">Title:</label>' +
                    '<input type="text" class="form-control titleInput pull-right" id="titleText"' + title + '>' +
                  '</div>';
        extraHTML += '<div class="form-group commentDiv">' +
                    '<textarea class="form-control" id="comment" rows="4" placeholder="Leave a review">' + comment + '</textarea>' +
                  '</div>';

        extraHTML += '<span id="errorTextRating" class="help-block"></span>';
        extraHTML += '<span id="errorTextTitle" class="help-block"></span>';
        extraHTML += '<span id="errorTextComment" class="help-block"></span>';
        extraHTML += '<span id="successMessage" class="help-block"></span>';

        extraHTML += '<button id="ratingButton" class="btn btn-default pull-right" title="Save review" type="button" onclick="submit()">' +
                        buttonText + '</button>'

        output += baseballCard.getStarSection(ratingIndex, score, legend, false, extraHTML);

        output += '</li>';

        return output;
    },

    createRatingDisplay : function(rating, ratingIndex) {
        var output = '';
        output += '<li class="row-lg-12 ratingsDisplay">';

        var legend = rating.user + "'s rating:";

        extraHTML = '<div class="form-group titleDiv">' +
                    '<input type="text" class="form-control titleInput pull-right" disabled="true" id="titleText" value="' +
                        rating.title + '">' +
                  '</div>';
        extraHTML += '<div class="form-group commentDiv">' +
                    '<textarea class="form-control" id="comment" rows="4" disabled="true">' + rating.comment + '</textarea>' +
                  '</div>';


        output += baseballCard.getStarSection(ratingIndex, rating.rating, legend, true, extraHTML);

        output += '</li>';

        return output;
    },

    getStarSection : function(ratingIndex, score, legend, readonly, extraHTML) {
        var addedSection = '';
        if(typeof extraHTML !== 'undefined') {
            addedSection = extraHTML;
        }

        var output = '<div id="ratingForm" class=form-group>' +
                        '<fieldset class="rating">' +
                            '<legend class="starLegend">' + legend + '</legend>';
        for(var i = 5; i >= 1; i--) {
            output += baseballCard.getStar(i, ratingIndex, score, readonly)
        }

        output += '</fieldset>' + addedSection + '</div>';

        return output;
    },

    getStar : function(starIndex, ratingIndex, score, readonly) {
        var checked = '';
        if(starIndex == score) {
            checked = ' checked="checked"';
        }
        var disabled = '';
        if(readonly) {
            disabled = ' disabled="true"';
        }

        return '<input type="radio" id="star' + starIndex + ratingIndex +
                '" name="rating' + ratingIndex + '"" value="' + starIndex + '"' + checked + disabled +
                 '/><label for="star' + starIndex + ratingIndex + '"> </label>';
    }
};