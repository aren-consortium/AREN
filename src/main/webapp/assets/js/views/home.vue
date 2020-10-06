<template>
    <div class="scroll-content" id="home">
        <div id="welcomeCallout" class="row light-color valign-wrapper">
            <img class="col" src="assets/img/imageleves.png">
                <p class="col" v-html="$t('helper.welcome')"></p>
        </div>

        <div class="container">

            <h1>Catégories</h1>

            <div class="row center-align" id="categoriesList">

                <div v-for="category in categories" v-bind:key="category.id" class="col">
                    <router-link v-bind:to="'/debates?category='+category.id" class="card hoverable center-align">
                        <div class="card-image light-color valign-wrapper">
                            <img class="category-picture" v-bind:src="category.picture">
                        </div>
                        <div>
                            <h2 v-bind:title="category.name">{{ category.name }}</h2>
                            <p>{{ $tc("nb_debates", category.debatesCount) }}</p>
                        </div>
                        <div class="card-footer light-color">
                            <span>{{ category.lastCommentDate !== 0 ? $t("last_post_the") + " " + $d(category.lastCommentDate, 'short') : $t('no_post') }}</span>
                        </div>
                    </router-link>
                </div>

            </div>

        </div>
    </div>
</template>

<script>
    module.exports = {
        data( ) {
            return {
                categories: false
            };
        },
        created( ) {
            this.fetchData( );
        },
        methods: {
            fetchData( ) {
                ArenService.Categories.getAll({
                    query: {overview: true},
                    onSuccess: (categories) => this.categories = categories
                });
            }
        }
    };
</script>