<template>
    <modal-layout>

        <template v-slot:title>{{ category.id ? $t('category_edition') : $t('category_creation') }}</template>

        <div class="card-image light-color  valign-wrapper">
            <img v-if="category.picture" class="category-picture" v-bind:src="category.picture"/>
        </div>
        <div>
            <text-input type="text"
                        v-model="category.name"
                        v-bind:placeholder="$t('helper.title_placeholder')"
                        v-bind:label="$t('title')">
            </text-input>
            <text-input type="text"
                        v-model="category.picture"
                        v-bind:placeholder="$t('helper.image_url_placeholder')"
                        v-bind:label="$t('image')">
            </text-input>
        </div>

        <template v-slot:footer>
            <button @click="close( )" class="waves-effect waves-green btn-flat">{{$t('cancel')}}</button>
            <button @click="close( category );" class="waves-effect waves-green btn-flat">{{$t('validate')}}</button>
        </template>

    </modal-layout>
</template>

<script>
    module.exports = {
        mixins: [VueModal],
        data( ) {
            return {
                category: new Category()
            };
        }
    };
</script>