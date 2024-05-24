package com.example.unilink.callback;

import com.example.unilink.objects.Product;

import java.util.ArrayList;

public interface ProductListCallback {
    void response(ArrayList<Product> list);
}
